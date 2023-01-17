.PHONY: clean oraclecloud-artefacts aws-artefacts

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
aws-artefacts:
	mkdir -p ${VERSION}/charts
	cp ./aws-cloudformation/* ./${VERSION}
	helm package -d ${VERSION}/charts ./helm-chart/mushop
	helm repo index ${VERSION}/charts
	zip -rm mushop-aws-v${VERSION}.zip ${VERSION}
	cp mushop-aws-v${VERSION}.zip mushop-aws-latest.zip
