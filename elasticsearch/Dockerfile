# Dockerfile
FROM docker.elastic.co/elasticsearch/elasticsearch:9.2.1

# Nori 플러그인 설치 명령 추가
RUN elasticsearch-plugin install analysis-nori

# 기본 ENTRYPOINT (Elasticsearch 실행)를 사용하도록 별도 ENTRYPOINT 지정 안함