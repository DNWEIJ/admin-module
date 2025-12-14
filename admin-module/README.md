Security setup

Member is our tenant. Tenant setup is as follows:
for save and update we use: TenantDiscriminator.java annotation that has a implementation in TenantEntityListener.java

For read we use

This means: IF we write native SQL queries, we need to use tenant id explicitly.