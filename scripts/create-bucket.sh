#!/bin/sh

MC=/opt/bitnami/minio-client/bin/mc

add_bucket()
{
  BUCKET_NAME=$1
  $MC rm -r --force localminio/$BUCKET_NAME # Allowed to fail when no bucket is available
  $MC mb --ignore-existing localminio/$BUCKET_NAME || exit 1
  $MC anonymous set public localminio/$BUCKET_NAME || exit 1
}

echo "Adding buckets..."
add_bucket 'archive'
echo "Finished adding buckets"
