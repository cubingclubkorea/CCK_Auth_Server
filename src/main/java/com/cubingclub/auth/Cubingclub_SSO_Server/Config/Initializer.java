package com.cubingclub.auth.Cubingclub_SSO_Server.Config;

/*
@Component
@RequiredArgsConstructor
public class Initializer {
    private final AccountRepository accountRepository;
    private final OAuthClientRepository oAuthClientRepository;
    private final OAuthClientService oAuthClientService;
    private final RsaKeysRepository rsaKeysRepository;
    private final RsaKeysService rsaKeysService;
    private final BCryptPasswordEncoder encoder;

    @Value("${spring.oauth.client.secret}")
    private String clientSecret;
    @Value("${spring.oauth.client.redirect-uri}")
    private String clientRedirect;

    @PostConstruct
    public void initiator() throws Exception {
        // 1. RSA Keys
        String keyId = "bomkey";
        if(rsaKeysRepository.findByIdentifier(keyId).isEmpty()) {
            rsaKeysService.saveRsaKeys(keyId);
            System.out.println("✅ RSA Keys created for: " + keyId);
        }

        Account account=new Account("cubingclubkorea@gmail.com", "관리자", "Administrator", encoder.encode("clientSecret"), UserRole.ROOT, null, null, Gender.Unknown, "CCK25ADMI01", null, null);
        accountRepository.save(account);

        // 2. OAuth Client
        String clientId = "cck_ranking";
        if(oAuthClientRepository.findByClientId(clientId).isEmpty()) {
            OAuthClientCreateDTO rankingRequest=new OAuthClientCreateDTO();
            rankingRequest.setClientId(clientId);
            rankingRequest.setClientName(clientId);
            rankingRequest.setClientDescription("CCK competition's record uploader, viewer");
            rankingRequest.setClientUri("https://ranking.cubingclub.com");
            rankingRequest.setRedirectUri(clientRedirect);
            rankingRequest.setPostLogoutRedirectUri("https://ranking.cubingclub.com");
            rankingRequest.setRequireAuthorizationConsent(true);
            rankingRequest.setClientType(ClientType.NORMAL);
            rankingRequest.setScopes(List.of("openid", "profile", "email")); // 한 번에 scope 설정
            oAuthClientService.saveOAuthClient(rankingRequest);

            System.out.println("✅ OAuth Client created: " + clientId);
        }
        oAuthClientService.updateSecretDefault(clientId, clientSecret, 365);
        clientId = "cck_recorder";
        if(oAuthClientRepository.findByClientId(clientId).isEmpty()) {
            OAuthClientCreateDTO recorderRequest=new OAuthClientCreateDTO();
            recorderRequest.setClientId(clientId);
            recorderRequest.setClientName(clientId);
            recorderRequest.setClientDescription("CCK record upload IoT object");
            recorderRequest.setClientUri("");
            recorderRequest.setPostLogoutRedirectUri("");
            recorderRequest.setRequireAuthorizationConsent(true);
            recorderRequest.setClientType(ClientType.IOT);
            oAuthClientService.saveOAuthClient(recorderRequest);
            oAuthClientService.updateSecretDefault(clientId, clientSecret, 365);
            System.out.println("✅ OAuth Client created: " + clientId);
        }
    }
}
*/