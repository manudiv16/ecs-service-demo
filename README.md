## App and Docker dummy
- Firstly, I created an API in Scala with two endpoints, one `/hello` and the other `/healthz`. The first one corresponds to the entry point that returns a simple *'Hello Word!'* and the second one is the endpoint to check if the service is up.
- To create the docker dummy I used the OpenJDK image to make a multistage Dockerfile wherein the first stage I compiled the application and, in the second one, I executed it. I did this to have a slightly smaller image. I used an image previously scanned by Docker to verify the vulnerabilities. Finally, I configured a *healthcheck* that uses the endpoint that will later use the *targetgroup* in Cloudformation.
## Cloudfomation
### IAM
1. I began creating some IAM roles. I created the first one to place the policies that will later serve to give the Task permissions to launch the logs in the Cloudwatch service that we will create later.
2. Then I created another role for the autoscaling application that I will define in the **api.yml** file.
I exported the two roles to use them later.
### VPC
- In this section, I created the virtual network for the cluster, and I used it for these two availability zones. In this case `[eu-central-1a, eu-central-1b]`, since I created my stack in `eu-central-1`.
- I exported the VPC and the subnets to refer to them later in the Service and the Loadbalancer.
### App-cluster
In this file, I centered all the base resources of the application, such as the ECS-Cluster, LoudBalancer with its respective entries, CloudWatch to launch the logs, and a Target Group for the application with a *healthcheck* that is the same path defined in the Dockerfile. 
I decided to incorporate HTTPS in the application and for this, I configured the listener with port 80 with the default action of sending all the traffic to port 443, which has as default the target group that the application will use.
To incorporate a certificate I incorporated a parameter called AcmCertificate referring to an Arn of a certificate located in the *certification manager*.
In this section I exposed:
- The target group to later refer to it in the service and direct the traffic to our application.
- The Cluster where the service will be located.
- The Security group for the same as the target group to refer to it from the service.
- LoadBalancerDNS. I used them to make it easier for me to take the domain name of the Loadbalancer and create a CNAME in my domain.
### App
In this document, I defined the resources dedicated to the application such as the Task, the Service, and the AplicationAutoScaling
- In the Task, I define the type of execution of the application that I decided to use Fargate since it allows me to execute directly the ECS tasks without having to create and control the EC2 instances.
- Later I defined the service, where I wrote that I want two instances and a maximum percent of 200 so that the service is never down if a deployment is made.
- For the application to be scalable, I created a scalable target so that the application will scale automatically, throughout the policy created in the same file, where I define that, when the service reaches 50% of the CPU use, it creates another instance. In this case, I put a CPU metric as an example.