#!/usr/bin/env bash

echo "Running day $1 Second problem"
kotlin -cp out/production/AoC2018/ day$1.second.SolutionKt < resources/day$1/input
