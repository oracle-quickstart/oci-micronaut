.PHONY: clean publish-aws

#
# Global configuration
BUCKET                  := micronaut-mushop-aws
REGION                  := us-west-2

#
# Include external configuration
VERSION                 = $(shell cat ./VERSION)

####################################
#              Targets             #
####################################
publish-aws:
	helm package -d ./aws-cloudformation ./helm-chart/mushop
	helm package -d ./aws-cloudformation ./helm-chart/provision
	aws s3 --recursive cp ./aws-cloudformation s3://$(BUCKET)/$(VERSION)


