# cs218-codepad

## Reference
https://www.youtube.com/watch?v=Gr2yTSsVSqg&t=1818s

## Creating the infrastructure
1. aws cloudformation create-stack --stack-name vpc --template-body file://$PWD/vpc.yaml
2. aws cloudformation create-stack --stack-name ecs-task-roles --template-body file://$PWD/ecs_task_role.yaml --capabilities CAPABILITY_IAM
3. aws cloudformation create-stack --stack-name app-cluster --template-body file://$PWD/app_cluster.yaml
4. aws cloudformation create-stack --stack-name tasks --template-body file://$PWD/service_and_task.yaml
