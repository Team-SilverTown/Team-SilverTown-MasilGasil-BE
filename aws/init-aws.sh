#!/usr/bin/env bash

awslocal s3api create-bucket \
    --bucket images
awslocal s3api put-bucket-policy \
    --bucket images \
    --policy file:///etc/localstack/json/policy.json
