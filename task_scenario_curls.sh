#!/usr/bin/env bash

set -euo pipefail

BASE_URL="${BASE_URL:-http://localhost:8080}"
USERNAME="${USERNAME:-testicula_user}"
PASSWORD="${PASSWORD:-testicula_user}"

TRACK_NAME="${TRACK_NAME:-Curl Scenario Track}"
TRACK_DESCRIPTION="${TRACK_DESCRIPTION:-Track for curl scenario}"
PET_ID="${PET_ID:-pet-curl-demo}"
SPRINT_LENGTH="${SPRINT_LENGTH:-14}"
START_DATE="${START_DATE:-2030-01-01}"
TARGET_DATE="${TARGET_DATE:-2030-01-28}"
MESSAGE_POLICY="${MESSAGE_POLICY:-NONE}"
TRACK_STATUS="${TRACK_STATUS:-ACTIVE}"

TASK_TITLE_PREFIX="${TASK_TITLE_PREFIX:-Task}"
TASK_DESCRIPTION_PREFIX="${TASK_DESCRIPTION_PREFIX:-Scenario task}"

require_cmd() {
  if ! command -v "$1" >/dev/null 2>&1; then
    echo "Missing required command: $1" >&2
    exit 1
  fi
}

pretty_print() {
  if jq . >/dev/null 2>&1 <<<"$1"; then
    jq . <<<"$1"
  else
    printf '%s\n' "$1"
  fi
}

request_json() {
  local method="$1"
  local url="$2"
  local data="${3:-}"

  if [[ -n "$data" ]]; then
    curl -sS -X "$method" \
      "$url" \
      -H "Authorization: Bearer $TOKEN" \
      -H "Content-Type: application/json" \
      -d "$data"
  else
    curl -sS -X "$method" \
      "$url" \
      -H "Authorization: Bearer $TOKEN"
  fi
}

require_cmd curl
require_cmd jq

echo "Fetching JWT token for $USERNAME..."
TOKEN_RESPONSE="$(
  curl -sS -X POST \
    "$BASE_URL/grind/keycloak/token" \
    -H "Content-Type: application/json" \
    -d "{\"username\":\"$USERNAME\",\"password\":\"$PASSWORD\"}"
)"
pretty_print "$TOKEN_RESPONSE"

TOKEN="$(jq -r '.access_token // empty' <<<"$TOKEN_RESPONSE")"
if [[ -z "$TOKEN" ]]; then
  echo "Failed to extract access_token from token response" >&2
  exit 1
fi

echo
echo "Creating track..."
TRACK_RESPONSE="$(
  request_json POST "$BASE_URL/api/core/track" \
    "{\"name\":\"$TRACK_NAME\",\"description\":\"$TRACK_DESCRIPTION\",\"petId\":\"$PET_ID\",\"sprintLength\":$SPRINT_LENGTH,\"startDate\":\"$START_DATE\",\"targetDate\":\"$TARGET_DATE\",\"messagePolicy\":\"$MESSAGE_POLICY\",\"status\":\"$TRACK_STATUS\"}"
)"
pretty_print "$TRACK_RESPONSE"

TRACK_ID="$(jq -r '.id // empty' <<<"$TRACK_RESPONSE")"
if [[ -z "$TRACK_ID" ]]; then
  echo "Failed to extract track id" >&2
  exit 1
fi

echo
echo "Fetching track sprints..."
SPRINTS_RESPONSE="$(request_json GET "$BASE_URL/api/core/track/sprints/$TRACK_ID")"
pretty_print "$SPRINTS_RESPONSE"

SPRINT_ID="$(jq -r '.[0].id // empty' <<<"$SPRINTS_RESPONSE")"
if [[ -z "$SPRINT_ID" ]]; then
  echo "Failed to extract sprint id" >&2
  exit 1
fi

declare -a TASK_IDS=()

for i in $(seq 1 10); do
  echo
  echo "Creating task $i..."
  TASK_RESPONSE="$(
    request_json POST "$BASE_URL/api/core/task" \
      "{\"title\":\"$TASK_TITLE_PREFIX $i\",\"description\":\"$TASK_DESCRIPTION_PREFIX $i\",\"trackId\":\"$TRACK_ID\"}"
  )"
  pretty_print "$TASK_RESPONSE"

  TASK_ID="$(jq -r '.id // empty' <<<"$TASK_RESPONSE")"
  if [[ -z "$TASK_ID" ]]; then
    echo "Failed to extract task id for task $i" >&2
    exit 1
  fi
  TASK_IDS+=("$TASK_ID")
done

for i in 0 1 2 3 4 5; do
  echo
  echo "Planning task $((i + 1))..."
  RESPONSE="$(
    request_json PUT "$BASE_URL/api/core/task/${TASK_IDS[$i]}/plan/sprint" \
      "{\"sprintId\":\"$SPRINT_ID\",\"dayOfSprint\":$i}"
  )"
  pretty_print "$RESPONSE"
done

for i in 8 9; do
  echo
  echo "Deleting task $((i + 1))..."
  RESPONSE="$(request_json DELETE "$BASE_URL/api/core/task/${TASK_IDS[$i]}")"
  pretty_print "$RESPONSE"
done

for i in 0 1 2 3; do
  echo
  echo "Completing task $((i + 1))..."
  RESPONSE="$(request_json PUT "$BASE_URL/api/core/task/${TASK_IDS[$i]}/complete")"
  pretty_print "$RESPONSE"
done

for i in 0 1; do
  echo
  echo "Moving task $((i + 1)) back to backlog..."
  RESPONSE="$(request_json PUT "$BASE_URL/api/core/task/${TASK_IDS[$i]}/backlog")"
  pretty_print "$RESPONSE"
done

echo
echo "Fetching final track tasks..."
FINAL_RESPONSE="$(request_json GET "$BASE_URL/api/core/task/track/$TRACK_ID")"
pretty_print "$FINAL_RESPONSE"

echo
echo "Summary:"
echo "TRACK_ID=$TRACK_ID"
echo "SPRINT_ID=$SPRINT_ID"
for i in "${!TASK_IDS[@]}"; do
  echo "TASK_$((i + 1))_ID=${TASK_IDS[$i]}"
done
