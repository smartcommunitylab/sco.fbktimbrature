Quick run

```
java -Dspring.profiles.active=prod,sec -jar mobile-attendance.jar --aac.clientId=<CLIENT_ID> --aac.clientSecret=<CLIENT_SECRET> --aac.attendanceScopes.read=<ACCEPTED_READ_SCOPES> --aac.attendanceScopes.write=<ACCEPTED_WRITE_SCOPES> --aac.url=<AAC_URL>
```
