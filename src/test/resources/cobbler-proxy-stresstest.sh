#!/bin/bash
#

if [ -z "${NO_RUN}" ]; then

  for i in {0..9}
  do
    for j in {0..9}
    do
     sudo cobbler system add --name=test${i}${j} --profile=RHEL6-x86_64 --mac=AA:BB:CC:DD:EE:${j}${i} &
    done
  done
fi

if [ -z "${NO_CLEAN}" ]; then
  for i in {0..9}
  do
    for j in {0..9}
    do
      sudo cobbler system remove --name=test${i}${j}
    done
  done
fi
