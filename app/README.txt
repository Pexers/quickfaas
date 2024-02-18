
		QuickFaaS

[1] System Requirements -------------

QuickFaaS was developed using Kotlin and Jetpack Compose as a self-contained desktop application, meaning that no JDK needs to be installed on the target system in order to run it.

The 'function-deployment' folder already includes a Maven version to build and deploy Java projects to FaaS platforms.
Node.js is not yet supported.

Keep in mind that this version of QuickFaaS is only a prototype.

[2] Prerequisites to enable FaaS deployments -------------

Google Cloud Platform
    In order to deploy cloud-agnostic functions through QuickFaaS to Google Cloud Platform, you will first need:
    (i)  Google account.
    (ii) Project in Google Cloud Platform responsible for managing all types of services.
    (iii)   Billing Account, that can be linked to multiple projects.
    (iv) Bucket, responsible for storing function sources.
    (v)  The Cloud Functions API enabled.
    (vi) The Cloud Resource Manager API enabled - allows to programmatically manage metadata for GCP resources

Microsoft Azure
    In order to deploy cloud-agnostic functions through QuickFaaS to Microsoft Azure, you will first need:
    (i)  Microsoft account.
    (ii) Azure account.
    (iii)   At least one active Subscription associated with the Azure account.
    (iv) Resource Group to hold various types of resources.
    (v)  Storage Account, responsible for storing function sources.

[3] References -------------
- Native distributions using Desktop Compose:
    - https://github.com/JetBrains/compose-jb/blob/master/tutorials/Native_distributions_and_local_execution/README.md#gradle-plugin
