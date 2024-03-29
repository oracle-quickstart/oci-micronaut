{{/* vim: set filetype=mustache: */}}
{{/*
Expand the name of the chart.
*/}}
{{- define "carts.name" -}}
{{- default .Chart.Name .Values.nameOverride | trunc 63 | trimSuffix "-" -}}
{{- end -}}

{{/*
Create a default fully qualified app name.
We truncate at 63 chars because some Kubernetes name fields are limited to this (by the DNS naming spec).
If release name contains chart name it will be used as a full name.
*/}}
{{- define "carts.fullname" -}}
{{- if .Values.fullnameOverride -}}
{{- .Values.fullnameOverride | trunc 63 | trimSuffix "-" -}}
{{- else -}}
{{- $name := default .Chart.Name .Values.nameOverride -}}
{{- if contains $name .Release.Name -}}
{{- .Release.Name | trunc 63 | trimSuffix "-" -}}
{{- else -}}
{{- printf "%s-%s" .Release.Name $name | trunc 63 | trimSuffix "-" -}}
{{- end -}}
{{- end -}}
{{- end -}}

{{/*
Create chart name and version as used by the chart label.
*/}}
{{- define "carts.chart" -}}
{{- printf "%s-%s" .Chart.Name .Chart.Version | replace "+" "_" | trunc 63 | trimSuffix "-" -}}
{{- end -}}

{{/*
Common labels
*/}}
{{- define "carts.labels" -}}
app.kubernetes.io/name: {{ include "carts.name" . }}
helm.sh/chart: {{ include "carts.chart" . }}
app.kubernetes.io/instance: {{ .Release.Name }}
{{- if .Chart.AppVersion }}
app.kubernetes.io/version: {{ .Chart.AppVersion | quote }}
{{- end }}
app.kubernetes.io/managed-by: {{ .Release.Service }}
{{- end -}}


{{/* OADB Connection environment */}}
{{- define "carts.oadb.connection" -}}
{{- $globalOsb := index (.Values.global | default .) "osb" -}}
{{- $usesOsbDb := (index (.Values.global | default .Values) "osb").atp | default .Values.osb.atp -}}
{{- $secretPrefix := (and .Values.osb.atp .Chart.Name) | default (and $globalOsb.atp ($globalOsb.instanceName | default "mushop")) | default .Chart.Name -}}
{{- $connectionSecret := (and $usesOsbDb (printf "%s-oadb-connection" $secretPrefix)) | default .Values.oadbConnectionSecret | default (.Values.global.oadbConnectionSecret | default (printf "%s-oadb-connection" $secretPrefix)) -}}
{{- $credentialSecret := (and $usesOsbDb (printf "%s-oadb-credentials" $secretPrefix)) | default .Values.oadbUserSecret | default (printf "%s-oadb-credentials" $secretPrefix) -}}
- name: ORACLECLOUD_ATP_USERNAME
  {{- if $globalOsb.atp }}
  value: {{ printf "mu_%s_user" .Chart.Name }}
  {{- else }}
  valueFrom:
    secretKeyRef:
      name: {{ $credentialSecret }}
      key: oadb_user
  {{- end }}
- name: ORACLECLOUD_ATP_PASSWORD
  valueFrom:
    secretKeyRef:
      name: {{ $credentialSecret }}
      key: oadb_pw
- name: ORACLECLOUD_ATP_WALLET_PASSWORD
  valueFrom:
    secretKeyRef:
      name: {{ $connectionSecret }}
      key: oadb_wallet_pw
- name: ORACLECLOUD_ATP_OCID
  valueFrom:
    secretKeyRef:
      name: {{ $connectionSecret }}
      key: oadb_ocid
- name: ORACLECLOUD_ATP_WALLET_SERVICE
  valueFrom:
    secretKeyRef:
      name: {{ $connectionSecret }}
      key: oadb_service
- name: ORACLECLOUD_ATP_HOST
  valueFrom:
    secretKeyRef:
      name: {{ $connectionSecret }}
      key: oadb_host
{{- end -}}

{{/* OADB ADMIN environment */}}
{{- define "carts.oadb.admin" -}}
{{- $globalOsb := index (.Values.global | default .) "osb" -}}
{{- $usesOsbDb := (index (.Values.global | default .Values) "osb").atp | default .Values.osb.atp -}}
{{- $secretPrefix := (and .Values.osb.atp .Chart.Name) | default (and $globalOsb.atp ($globalOsb.instanceName | default "mushop")) | default .Chart.Name -}}
{{- $adminSecret := (and $usesOsbDb (printf "%s-oadb-admin" $secretPrefix)) | default .Values.oadbAdminSecret | default .Values.global.oadbAdminSecret | default (printf "%s-oadb-admin" $secretPrefix) -}}
- name: ORACLECLOUD_ATP_ADMIN_PASSWORD
  valueFrom:
    secretKeyRef:
      name: {{ $adminSecret }}
      key: oadb_admin_pw
{{- end -}}

{{/* OADB Wallet mount */}}
{{- define "carts.mount.wallet" -}}
# for init container
- name: wallet
  mountPath: /usr/lib/oracle/21/client64/lib/network/admin/
  readOnly: true
{{- end -}}

{{/* OADB Wallet BINDING initContainer */}}
{{- define "carts.init.wallet" -}}
{{- $usesOsb := (index (.Values.global | default .Values) "osb").atp | default .Values.osb.atp -}}
{{- if $usesOsb }}
# OSB Wallet Binding decoder
- name: decode-binding
  image: oraclelinux:7-slim
  command: ["/bin/sh","-c"]
  args:
  - for i in `ls -1 /tmp/wallet | grep -v user_name`; do cat /tmp/wallet/$i | base64 --decode > /wallet/$i; done; ls -l /wallet/*;
  volumeMounts:
    - name: wallet-binding
      mountPath: /tmp/wallet
      readOnly: true
    - name: wallet
      mountPath: /wallet
      readOnly: false
{{- end -}}
{{- end -}}

{{/* OADB dbtools mount template */}}
{{- define "carts.mount.initdb" -}}
- name: initdb
  mountPath: /work/
{{- end -}}

{{/* CONTAINER VOLUME TEMPLATE */}}
{{- define "carts.volumes" -}}
{{- $globalOsb := index (.Values.global | default .) "osb" -}}
{{- $wallet := .Values.oadbWalletSecret | default (.Values.global.oadbWalletSecret | default (printf "%s-oadb-wallet" .Chart.Name)) -}}
{{- $walletBinding :=  printf "%s-oadb-wallet-binding" ((and .Values.osb.atp .Chart.Name) | default $globalOsb.instanceName | default "mushop") -}}
{{- if or .Values.osb.atp $globalOsb.atp }}
# OSB wallet binding
- name: wallet-binding
  secret:
    secretName: {{ $walletBinding }}
- name: wallet
  emptyDir: {}
{{- else }}
# local wallet
- name: wallet
  secret:
    secretName: {{ $wallet }}
    defaultMode: 256
{{- end }}
# service init configMap
- name: initdb
  configMap:
    name: {{ include (printf "%s.fullname" .Chart.Name) . }}-init
    items:
    - key: atp.init.sql
      path: service.sql
{{- end -}}

{{/* OAPM Connection url */}}
{{- define "carts.oapm.connection" -}}
{{- $oapmConnection := .Values.oapmConnectionSecret | default (.Values.global.oapmConnectionSecret | default (printf "%s-oapm-connection" .Release.Name)) -}}
- name: ORACLECLOUD_TRACING_ZIPKIN_HTTP_URL
  valueFrom:
    secretKeyRef:
      name: {{ $oapmConnection }}
      key: zipkin_url
- name: ORACLECLOUD_TRACING_ZIPKIN_HTTP_PATH
  valueFrom:
    secretKeyRef:
      name: {{ $oapmConnection }}
      key: zipkin_path
- name: TRACING_ZIPKIN_ENABLED
  valueFrom:
    secretKeyRef:
      name: {{ $oapmConnection }}
      key: zipkin_enabled
{{- end -}}

{{/* OIMS configuration */}}
{{- define "carts.oims.config" -}}
{{- $ociDeployment := .Values.ociDeploymentConfigMap | default (.Values.global.ociDeploymentConfigMap | default (printf "%s-oci-deployment" .Chart.Name)) -}}
- name: ORACLECLOUD_METRICS_COMPARTMENT_ID
  valueFrom:
    configMapKeyRef:
      name: {{ $ociDeployment }}
      key: compartment_id
{{- end -}}