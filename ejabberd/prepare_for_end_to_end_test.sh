#!/bin/bash
DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" >/dev/null 2>&1 && pwd )"

# Create 3 users:
#  - sniper@localhost:sniper
#  - auction-item-54321@localhost:auction
#  - auction-item-65432@localhost:auction
$DIR/cmd.sh register sniper localhost sniper
$DIR/cmd.sh register auction-item-54321 localhost auction
$DIR/cmd.sh register auction-item-65432 localhost auction



