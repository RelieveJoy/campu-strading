# Elasticsearch 7.17.25 + IK 中文分词插件
# 说明：GitHub 下载被墙，改用 InfiniLabs 官方发布源安装
FROM docker.elastic.co/elasticsearch/elasticsearch:7.17.25

# --batch 跳过权限确认；安装版本需与 ES 主版本严格一致（7.17.25）
RUN bin/elasticsearch-plugin install --batch \
    https://release.infinilabs.com/analysis-ik/stable/elasticsearch-analysis-ik-7.17.25.zip
