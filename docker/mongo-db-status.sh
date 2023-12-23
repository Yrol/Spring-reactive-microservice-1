#!/bin/bash
# mongo-db-status.sh

apt-get update -y

yes | apt-get install curl

curlResult=$(curl -s -o /dev/null -I -w "%{http_code}" http://mongodb:27017)

echo "results status code:" $curlResult

while [[ ! $curlResult == "200" ]]; do
  >&2 echo "Mongo DB is not up yet!"
  sleep 1
  curlResult=$(curl -s -o /dev/null -I -w "%{http_code}" http://mongodb:27017)
done

echo "Mongo DB is is up!"

/cnb/process/web