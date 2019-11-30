## Pushing a docker image to ECR

1. Create repository in ECR  using AWS CLI
aws ecr create-repository --repository-name codepad

2. Get login required for ECR
aws ecr get-login --no-include-email

3. Step-2 will return a ‘docker login’ command with inline password, copy paste the entire output of step-2 on the shell to login to the ECR (Docker registry).

4. Retrieve repository information
aws ecr describe-repositories

5. Tag the docker image with the repository URI (retrieved from step-4) and the version number
docker tag codepad/codepad-api-gateway:0.0.1-SNAPSHOT 447551633800.dkr.ecr.us-east-1.amazonaws.com/codepad:v1

6. Push the image to ECR
docker push 447551633800.dkr.ecr.us-east-1.amazonaws.com/codepad:v1

