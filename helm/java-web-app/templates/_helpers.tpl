{{/*
Expand the chart name.
*/}}
{{- define "java-web-app.name" -}}
{{- .Chart.Name }}
{{- end }}

{{/*
Create a fully-qualified name.
We use the release name as the base so:
  helm install java-web-app ./helm/java-web-app
produces resources named  java-web-app-blue / java-web-app-green / java-web-app-svc
*/}}
{{- define "java-web-app.fullname" -}}
{{- .Release.Name | trunc 63 | trimSuffix "-" }}
{{- end }}

{{/*
Common labels attached to every resource.
*/}}
{{- define "java-web-app.labels" -}}
helm.sh/chart: {{ .Chart.Name }}-{{ .Chart.Version }}
app.kubernetes.io/name: {{ include "java-web-app.name" . }}
app.kubernetes.io/instance: {{ .Release.Name }}
app.kubernetes.io/managed-by: {{ .Release.Service }}
{{- end }}
