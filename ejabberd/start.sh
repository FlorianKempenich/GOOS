#!/bin/bash
DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" >/dev/null 2>&1 && pwd )"

docker run \
    -d \
    --name ejabberd \
    --rm \
    -p 5222:5222 \
    ejabberd/ecs
