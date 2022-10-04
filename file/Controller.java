@Controller
class BillingController {
    private val objectMapper = ObjectMapper()

    @RequestMapping("/success")
    fun success(
        /**
         * 1. Request Parameter 정보
         *
         * - authKey: 빌링키를 얻을 때 사용하는 인증 키
         * - customerKey: 가맹점에서 사용하는 사용자별 고유 ID (내부에서 사용하는 카드 소유주별 식별자)
         */
        @RequestParam requestParams: Map<String, String>,
        model: Model,
    ): String {
        /**
         * 2. 발급 받은 시크릿 키 Base64 인코딩
         */
        val encodedAuthHeader = Base64.getEncoder().encodeToString(("$SECRET_KEY:").toByteArray())

        /**
         * 3. 빌링키 발급 Reequest 생성
         */
        val request: HttpRequest = HttpRequest.newBuilder()
            .uri(URI.create("https://api.tosspayments.com/v1/billing/authorizations/${requestParams.getValue("authKey")}"))
            .header("Authorization", "Basic $encodedAuthHeader")
            .header("Content-Type", "application/json")
            .method("POST", HttpRequest.BodyPublishers.ofString("{\"customerKey\":\"${requestParams.getValue("customerKey")}\"}"))
            .build()

        val response: HttpResponse<String> =
            HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString())

        return if (response.statusCode() == OK.value()) {
            /**
             * 4. 빌링키, 카드 정보 포함된 Json 으로 성공 View 리턴
             */
            val jsonNode = objectMapper.readTree(response.body())
            model.addAttribute("response", jsonNode.toPrettyString())
            "success"
        } else {
            /**
             * 5. 실패 View 리턴
             */
            model.addAttribute("message", "카드 정보를 저장하는데 실패하였습니다.")
            "fail"
        }
    }

    @RequestMapping(value = ["/fail"])
    fun billingFail(
        /**
         * 1. Request Parameter 정보
         *
         * - code: 실패 코드
         * - customerKey: 실패 사유
         */
        @RequestParam(required = false) code: String?,
        @RequestParam(required = false) message: String?,
        model: Model,
    ): String {
        model.addAttribute("code", code)
        model.addAttribute("message", message)
        return "fail";
    }

    companion object {
        const val SECRET_KEY = "test_sk_zXLkKEypNArWmo50nX3lmeaxYG5R"
    }
}

@Setter
@Column
private String paymentKey;

@getMapping("/success")
@ApiOperation(value = "결제 성공 리다이렉트", notes = "결제 성공 시 최종 결제 승인 요청을 보냅니다.")

public SingleResult<String> requestFinalPayments(
    @ApiParam(value = "토스 측 결제 고유 번호", required = true) @ReequestParam String paymentKey,
    @ApiParam(value = "우리 측 결제 고유 번호", required = true) @ReequestParam String orderId,
    @ApiParam(value = "실제 결제 금액", required = true) @RequestParam Long amount
)
    try{
        system.out.println("paymentKey = " paymentKey);
    system.out.println("orderId = " orderId);
    system.out.println("amount = " amount);
    
    paymentService.verifyRequst(paymentKey, orderId, amount);
    String result = paymentService.requestFinalPayments(paymentKey, orderId, amount);

    return responseService.getSingleResult(result);

    }catch (Exception e)
    {
        e.printStackTrace();
        throw new BussinessException(e.getMessage());
    }


@Transactional
public void verifyRequst(String paymentKey, String orderId, Long amount)
{
    paymentRepository.findByOrderId(orderId)
    .ifPresentOrElse(
        P -> {
            if(P.getAmount().equals(amount)){
                P.setPaymentKey(paymentKey);
            }
            else{
                throw new BussinessException(ExMessage.PAYMENT_ERROR_ORDER_AMOUNT);
            }
        }, () ->{
            throw new BussinessException(ExMessage.UNDEFINED_ERROR);
        }
}                                                                               

public String requestFinalPayment(String paymentKey, String orderId, Long amount)
{
    RestTemplate rest = new RestTemplate();

    HttpHeaders headers = new HttpHeaders();

    testSecretApiKey = testSecretApiKey + ":";
    String encodeAuth = new String(Base64.getEncoder().encode(testSecretApiKey.getBytes(StandardCharsets.UTF_8)));
    headers.setBasicAuth(encodeAuth);
    headers.setContentType(MediaType.APPLICATION_JSON);
    header.setAccept(Collections.singleletonList(MediaType.APPLICATION_JSON));

    JSONObject param = new JSONObject();
    param.put("orderID", orderId);
    param.put("amount", amount);

    return rest.postForEntity(
        tossOriginUrl + paymentKey,
        new HttpEntity<>(param, headers),
        String.class
    )
}
/*
public String requestFinalPayment(String paymentKey, String orderId, Long amount){
    RestTemplate rest = new RestTemplate();

    HttpHeaders headers = new HttpHeaders();

    String encodeAuth = new String(Base64.getEncoder().encode(testSecretApiKey.getBytes(StandardCharsets.UTF_8)));

    headers.setBasicAuth(encodedAuth);
    headers.setContentType(MediaType.APPLICATION_JSON);
    headers.setAcceot(Collections.singleletonList(MediaType.APPLICATION_JSON));

    MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
    params.add("orderID", orderID);
    params.add("amunt", amount+"");

    HttpEntity formEntity = new HttpEntity<>(params, headers);

    ResponseEntity<String> response = rest.postForEntity (
        tossOriginUrl + paymentKey,
        formEntity,
        String.class
    );
    return response.getBody();

    
}
*/


