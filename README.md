# QuickFaaS ☁️

QuickFaaS is a multi-cloud interoperability desktop tool targeting cloud-agnostic functions development and FaaS deployments. QuickFaaS substantially improves developers' productivity, flexibility and agility when creating serverless solutions to different cloud providers **without requiring the installation of provider-specific software**. 

The proposed cloud-agnostic approach enables developers to reuse their serverless functions in multiple cloud providers with **no need to rewrite code**. This solution aims to minimize vendor lock-in in FaaS platforms by increasing the portability of serverless functions, which will, therefore, encourage developers and organizations to target different vendors in exchange for a functional benefit.

<p align="center">
  <img src="https://user-images.githubusercontent.com/47757441/185813592-ed461efa-2c40-4d43-9024-d2cf3fc13324.png" width="360">
</p>

> [!NOTE]
> - QuickFaaS was originally developed within the context of a Master’s degree dissertation titled "_Characterizing and Providing Interoperability to FaaS Platforms_", at Instituto Superior de Engenharia de Lisboa (ISEL), Lisboa, Portugal.

Besides the application's source code, this repository also includes the uniform programming model for authentication and FaaS deployments to be used through the command-line, together with the cloud-agnostic libraries. An [evaluation](evaluation) is also provided in the form of Excel spreadsheets in order to validate the proposed solution by measuring the impact of a cloud-agnostic approach on the function's performance, when compared to a cloud-non-agnostic one.

Be sure to check out the [_wiki_](https://github.com/Pexers/quickfaas/wiki) page for more information regarding the usage of cloud-agnostic libraries.

## Publications 📰
#### _QuickFaaS: Providing Portability and Interoperability between FaaS Platforms_
- [[DOI](https://doi.org/10.3390/fi14120360)] Accepted for publication by MDPI in the peer-reviewed scientific journal _Future Internet_, within the special issue "_Distributed Systems for Emerging Computing: Platform and Application_".
- [[DOI](https://doi.org/10.1007/978-3-031-23298-5_6)] Included in the proceedings of the _9<sup>th</sup> European Conference On Service-Oriented And Cloud Computing_ (ESOCC), in the projects track, published by Springer in the _Communications in Computer and Information Science_ (CCIS) book series.

## Desktop application 💻
Authentication|Function Configuration|
:-------------------------:|:-------------------------:|
<kbd><img src="https://user-images.githubusercontent.com/47757441/209371994-3bfa1416-dd7a-482c-8031-4897dedf9df0.png" width="400"></kbd>|<kbd><img src="https://user-images.githubusercontent.com/47757441/209371997-8713343a-1942-4a37-a21f-aa554723b99f.png" width="400"></kbd>|

Function Definition|FaaS Deployment|
:-------------------------:|:-------------------------:|
<kbd><img src="https://user-images.githubusercontent.com/47757441/209371999-e5dd8e98-824f-444f-9394-9c16a47279f7.png" width="400"></kbd>|<kbd><img src="https://user-images.githubusercontent.com/47757441/209372001-5b8109b7-f975-46ef-b351-1d106666c9f8.png" width="400"></kbd>|

## System requirements
QuickFaaS was developed using Kotlin and Desktop Compose as a self-contained desktop application, meaning that no JDK needs to be installed on the target system in order to run it.

The `function-deployment` folders already includes a Maven version to build and deploy Java projects to FaaS platforms. Node.js is not yet supported.

## Enabling FaaS deployments
### Google Cloud Platform
In order to deploy cloud-agnostic functions through QuickFaaS to Google Cloud Platform, you will first need:
1.  Google account.
2.  Project in Google Cloud Platform responsible for managing all types of services.
3.  Billing Account, that can be linked to multiple projects.
4.  Bucket, responsible for storing function sources.
5.  The Cloud Functions API enabled.
6.  The Cloud Resource Manager API enabled - allows to programmatically manage metadata for GCP resources

### Microsoft Azure
In order to deploy cloud-agnostic functions through QuickFaaS to Microsoft Azure, you will first need:
1. Microsoft account.
2. Azure account.
3. At least one active Subscription associated with the Azure account.
4. Resource Group to hold various types of resources.
5. Storage Account, responsible for storing function sources.

## References
- [Native distributions](https://github.com/JetBrains/compose-jb/blob/master/tutorials/Native_distributions_and_local_execution/README.md#gradle-plugin) using Desktop Compose. 
