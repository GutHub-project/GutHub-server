# Dockerfile
FROM docker.elastic.co/elasticsearch/elasticsearch:8.18.1

# Nori 플러그인 설치 명령 추가
RUN elasticsearch-plugin install analysis-nori

ENTRYPOINT ["top", "-b"]