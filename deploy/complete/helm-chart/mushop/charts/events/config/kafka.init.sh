#!/usr/bin/env sh

ZOOKEPER_CONNETION_STRING=$(aws kafka describe-cluster --cluster-arn ${MKS_CLUSTER_ARN} -o json | jq -r '.ClusterInfo.ZookeeperConnectString')
./kafka/bin/kafka-topics.sh --create --if-not-exists --zookeeper ${ZOOKEPER_CONNETION_STRING} --replication-factor 3 --partitions 1 --topic events
