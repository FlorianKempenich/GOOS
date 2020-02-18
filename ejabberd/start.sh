#!/bin/bash
DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" >/dev/null 2>&1 && pwd )"

docker run \
    -d \
    --name ejabberd \
    --rm \
    -v $DIR/ejabberd.yml:/home/ejabberd/conf/ejabberd.yml \
    -p 5222:5222 \
    ejabberd/ecs
