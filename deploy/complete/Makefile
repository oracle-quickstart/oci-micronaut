.PHONY: clean publish-aws oraclecloud-build

#
# Global AWS configuration
AWS_S3_BUCKET                  := micronaut-mushop-aws
AWS_S3_REGION                  := us-west-2

#
# Include external configuration
VERSION                 = $(shell cat ./VERSION)

####################################
#              Targets             #
####################################

#
# Oracle Cloud
oraclecloud-artefacts:
	zip -r mushop-stack-v${VERSION}.zip  helm-chart terraform  -x "terraform/.terraform/*" "terraform/configs/*" "terraform/generated/*" "terraform/terraform.tfstate.d/*" "helm-chart/.gitignore" "terraform/setup*"
	cp mushop-stack-v${VERSION}.zip mushop-stack-latest.zip

#
# AWS
aws-publish:
	helm package -d ./aws-cloudformation ./helm-chart/mushop
	helm package -d ./aws-cloudformation ./helm-chart/provision
	aws s3 --recursive cp ./aws-cloudformation s3://$(AWS_S3_BUCKET)/$(VERSION)





