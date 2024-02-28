FROM localstack/localstack:3.2
LABEL authors="ASak1104"

COPY --chown=localstack ./init-aws.sh /etc/localstack/init/ready.d/init-aws.sh

RUN chmod u+x /etc/localstack/init/ready.d/init-aws.sh
