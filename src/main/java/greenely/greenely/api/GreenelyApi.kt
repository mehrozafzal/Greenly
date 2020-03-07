package greenely.greenely.api

import greenely.greenely.api.models.Response
import greenely.greenely.competefriend.json.addFriendJson.AddFriendResponseJson
import greenely.greenely.competefriend.json.inviteUserJson.InviteUserResponseJson
import greenely.greenely.competefriend.json.rankListJson.RankResponseJson
import greenely.greenely.feed.models.FeedCountResponse
import greenely.greenely.feed.models.FeedResponse
import greenely.greenely.forgotpassword.ForgotPasswordRequest
import greenely.greenely.guidance.models.json.GuidanceContentJson
import greenely.greenely.history.*
import greenely.greenely.home.models.FacilitiesResponse
import greenely.greenely.home.models.HomeResponseJson
import greenely.greenely.login.model.LoginData
import greenely.greenely.models.AuthenticationInfo
import greenely.greenely.profile.json.GetProfileResponse
import greenely.greenely.profile.json.SaveProfileRequest
import greenely.greenely.profile.json.SaveProfileResponse
import greenely.greenely.push.data.models.RegisterDeviceRequest
import greenely.greenely.registration.data.models.RegistrationRequest
import greenely.greenely.retail.models.*
import greenely.greenely.retailinvite.models.ReferralInviteResponseModel
import greenely.greenely.retailonboarding.models.*
import greenely.greenely.settings.data.ChangePasswordRequest
import greenely.greenely.settings.data.Household
import greenely.greenely.settings.data.NotificationSettingJson
import greenely.greenely.settings.data.SettingsInfoResponse
import greenely.greenely.setuphousehold.models.json.HouseholdConfigJsonModel
import greenely.greenely.setuphousehold.models.json.HouseholdRequestJsonModel
import greenely.greenely.signature.data.models.InputValidationModel
import greenely.greenely.signature.data.models.PrefillDataResponseModel
import greenely.greenely.signature.data.models.SignatureRequestModel
import greenely.greenely.signature.data.models.ValidationResponse
import greenely.greenely.solaranalysis.models.AddressValidationRequest
import greenely.greenely.solaranalysis.models.AnalysisResponseJson
import greenely.greenely.solaranalysis.models.ContactMeRequest
import greenely.greenely.solaranalysis.models.HouseholdInfoJson
import greenely.greenely.welcome.model.json.InvitedFriendJson
import io.reactivex.Completable
import io.reactivex.Observable
import retrofit2.http.*


interface GreenelyApi {

    @GET("/v3/onboarding")
    fun getHouseholdConfig(@Header("Authorization") jwt: String): Observable<Response<HouseholdConfigJsonModel>>

    @POST("/v3/onboarding")
    fun postHouseholdData(
            @Header("Authorization") jwt: String, @Body body: HouseholdRequestJsonModel
    ): Observable<Response<AuthenticationInfo>>

    @PUT("/v3/onboarding/household")
    fun patchHouseholdData(
            @Header("Authorization") jwt: String, @Body body: HouseholdRequestJsonModel
    ): Observable<Response<HouseholdRequestJsonModel>>

    @GET("/v3/onboarding/household")
    fun getHouseholdData(@Header("Authorization") jwt: String): Observable<Response<HouseholdRequestJsonModel>>

    @GET("/v1/checkauth")
    fun checkAuth(@Header("Authorization") jwt: String): Observable<Response<AuthenticationInfo>>

    @POST("/v1/user/register")
    fun register(@Body body: RegistrationRequest, @Query("invitation_id") invitationID: String?
    ): Observable<Response<AuthenticationInfo>>

    @POST("/v1/reset-password")
    fun sendEmail(@Body body: ForgotPasswordRequest): Completable

    @POST("/v1/login")
    fun login(@Body request: LoginData): Observable<Response<AuthenticationInfo>>

    @GET("/v1/feed/entries")
    fun getFeed(@Header("Authorization") token: String): Observable<Response<FeedResponse>>

    @GET("/v1/feed/entries/count/new")
    fun getFeedNewCount(@Header("Authorization") token: String): Observable<Response<FeedCountResponse>>

    @GET("/v3/data")
    fun getYear(@Header("Authorization") token: String): Observable<Response<YearResponse>>

    @GET("/v3/data/{year}/{month}/weeks")
    fun getWeeks(
            @Header("Authorization") token: String,
            @Path("year") year: Int,
            @Path("month") month: Int
    ): Observable<Response<WeekResponse>>

    @GET("/v3/data/{year}/{month}/usage")
    fun getMonthUsage(
            @Header("Authorization") token: String,
            @Path("year") year: Int,
            @Path("month") month: Int
    ): Observable<Response<UsageResponse>>

    @GET("/v3/data/{year}/{month}/{day}/usage")
    fun getDayUsage(
            @Header("Authorization") token: String,
            @Path("year") year: Int,
            @Path("month") month: Int,
            @Path("day") day: Int
    ): Observable<Response<UsageResponse>>

    @GET("/v3/data/{year}/{month}/temperature")
    fun getMonthTemperature(
            @Header("Authorization") token: String,
            @Path("year") year: Int,
            @Path("month") month: Int
    ): Observable<Response<TemperatureResponse>>

    @GET("/v3/data/{year}/{month}/{day}/temperature")
    fun getDayTemperature(
            @Header("Authorization") token: String,
            @Path("year") year: Int,
            @Path("month") month: Int,
            @Path("day") day: Int
    ): Observable<Response<TemperatureResponse>>


    @GET("/v3/data/{year}/{month}/price")
    fun getMonthPrice(
            @Header("Authorization") token: String,
            @Path("year") year: Int,
            @Path("month") month: Int
    ): Observable<Response<PriceResponse>>

    @GET("/v3/data/{year}/{month}/{day}/price")
    fun getDayPrice(
            @Header("Authorization") token: String,
            @Path("year") year: Int,
            @Path("month") month: Int,
            @Path("day") day: Int
    ): Observable<Response<PriceResponse>>


    @GET("/v3/data/{year}/{month}/distribution")
    fun getMonthDistribution(
            @Header("Authorization") token: String,
            @Path("year") year: Int,
            @Path("month") month: Int
    ): Observable<Response<List<DistributionDataPoint>>>

    @GET("/v3/data/{year}/{month}/min_max")
    fun getMonthMinMax(
            @Header("Authorization") token: String,
            @Path("year") year: Int,
            @Path("month") month: Int
    ): Observable<Response<MinMaxResponse>>


    @GET("/v3/data/{year}/{month}/{day}/distribution")
    fun getDayDistribution(
            @Header("Authorization") token: String,
            @Path("year") year: Int,
            @Path("month") month: Int,
            @Path("day") day: Int
    ): Observable<Response<List<DistributionDataPoint>>>

    @GET("/v3/data/{year}/{month}/{day}/min_max")
    fun getDayMinMax(
            @Header("Authorization") token: String,
            @Path("year") year: Int,
            @Path("month") month: Int,
            @Path("day") day: Int
    ): Observable<Response<MinMaxResponse>>

    @GET("/v1/settings")
    fun getSettingsInfo(@Header("Authorization") jwt: String): Observable<Response<SettingsInfoResponse>>

    @GET("/v2/settings/household")
    fun getHousehold(@Header("Authorization") jwt: String): Observable<Response<Household>>


    @GET("/v2/settings")
    fun getNotificationSettings(@Header("Authorization") jwt: String): Observable<Response<NotificationSettingJson>>

    @PATCH("/v2/settings")
    fun patchNotificationSettings(@Header("Authorization") jwt: String, @Body request: NotificationSettingJson): Observable<Response<NotificationSettingJson>>

    @POST("/v1/settings/changepassword")
    fun changePassword(
            @Header("Authorization") jwt: String, @Body request: ChangePasswordRequest
    ): Observable<Response<@JvmSuppressWildcards Any>>

    @GET("/v4/home")
    fun getHome(
            @Header("Authorization") jwt: String,
            @Query("resolution") resolution: String
    ): Observable<Response<HomeResponseJson>>

    @POST("/v2/guide/solar/estimate")
    fun postSolarAnalysisHousehold(
            @Header("Authorization") jwt: String,
            @Body householdInfo: HouseholdInfoJson
    ): Observable<Response<AnalysisResponseJson>>

    @POST("/v2/guide/solar/address")
    fun validateSolarAnalysisAddress(
            @Header("Authorization") jwt: String,
            @Body addressValidationRequest: AddressValidationRequest
    ): Observable<Response<@JvmSuppressWildcards Any>>

    @POST("/v2/guide/solar/contact")
    fun postContact(
            @Header("Authorization") jwt: String,
            @Body contactMeRequest: ContactMeRequest
    ): Observable<Response<@JvmSuppressWildcards Any>>


    @POST("/v1/retail/convert")
    fun postNewRetailCustomer(
            @Header("Authorization") jwt: String,
            @Body customerInfoJson: CustomerInfoJson
    ): Observable<Response<CustomerConversionResponseJson>>

    @POST("/v1/poa/sign")
    fun postNewPoaCustomer(
            @Header("Authorization") jwt: String,
            @Body signatureRequestModel: SignatureRequestModel
    ): Observable<Response<CustomerConversionResponseJson>>


    @GET("/v1/settings/faq")
    fun getFAQItems(
            @Header("Authorization") authToken: String
    ): Observable<Response<List<List<String>>>>

    @GET("/v1/guide/proxy")
    fun getGuidance(
            @Header("Authorization") authToken: String
    ): Observable<Response<GuidanceContentJson>>

    @POST("/v1/user/devices")
    fun registerDevice(
            @Header("Authorization") jwt: String,
            @Body body: RegisterDeviceRequest
    ): Observable<Response<@JvmSuppressWildcards Any>>

    //TODO: Refactor to be reusable because it's used in POA and Retail start with different models
    @GET("/v3/user/signature/address/{pno}")
    fun fetchSignaturePreFillData(
            @Header("Authorization") jwt: String,
            @Path("pno") personalNumber: String
    ): Observable<Response<PrefillDataResponseModel>>

    @POST("/v3/user/signature")
    fun sendSignature(
            @Header("Authorization") jwt: String,
            @Body body: SignatureRequestModel
    ): Observable<Response<@JvmSuppressWildcards Any>>

    @POST("/validation/v2")
    fun validateInputValues(
            @Body body: InputValidationModel
    ): Observable<Response<ValidationResponse>>

    @GET("/v2/retail/overview")
    fun getRetailOverview(
            @Header("Authorization") authToken: String
    ): Observable<Response<RetailOverViewJson>>

    @POST("/v1/retail/promocode/verify")
    fun verifyPromoCode(
            @Header("Authorization") jwt: String,
            @Body body: VerifyPromoCodeRequest
    ): Observable<Response<VerifyPromoCodeResponse>>


    @GET("/v1/retail/price-summary")
    fun getRetailPriceSummary(
            @Header("Authorization") authToken: String, @Query("promocode") promoCode: String?
    ): Observable<Response<RetailPriceSummaryResponse>>


    @GET("/v1/retail/referral")
    fun getRetailReferral(
            @Header("Authorization") authToken: String
    ): Observable<Response<ReferralInviteResponseModel>>

    @GET("/v2/retail/invoices")
    fun getRetailInvoices(
            @Header("Authorization") authToken: String
    ): Observable<Response<List<Invoice>>>

    @GET("/v1/retail/state")
    fun getRetailState(@Header("Authorization") authToken: String): Observable<Response<RetailStateResponseModel>>

    @Headers("CONNECT_TIMEOUT:2000", "READ_TIMEOUT:2000", "WRITE_TIMEOUT:2000")
    @GET("/v1/retail/bankid")
    fun getBankIdProcessStatus(
            @Header("Authorization") authToken: String,
            @Query("bankid_order_ref") bankIdOrderRef: String
    ): Observable<Response<RetailBankIdProcessJson>>


    @Headers("CONNECT_TIMEOUT:2000", "READ_TIMEOUT:2000", "WRITE_TIMEOUT:2000")
    @GET("/v1/poa/bankid")
    fun getPOABankIdProcessStatus(
            @Header("Authorization") authToken: String,
            @Query("bankid_order_ref") bankIdOrderRef: String
    ): Observable<Response<List<RetailBankIdProcessJson>>>

    @DELETE("/v1/retail/bankid")
    fun cancelExistingBankIDProcess(
            @Header("Authorization") authToken: String,
            @Query("bankid_order_ref") bankIdOrderRef: String
    ): Observable<Response<RetailBankIdProcessJson>>

    @DELETE("/v1/poa/bankid")
    fun cancelExistingPoaBankIDProcess(
            @Header("Authorization") authToken: String,
            @Query("bankid_order_ref") bankIdOrderRef: String
    ): Observable<Response<RetailBankIdProcessJson>>


    //Author: Muhammad Mehroz Afzal
    @GET("/v1/ranking")
    fun getRankedUserList(@Header("Authorization") authToken: String,
                          @Query("resolution") resolution: String,
                          @Query("ranking_method") rankingMethod: String):
            Observable<Response<RankResponseJson>>

    @POST("/v1/invitations")
    fun getInviteUser(@Header("Authorization") authToken: String):
            Observable<Response<InviteUserResponseJson>>

    @POST("/v1/friends")
    fun addFriendToList(@Header("Authorization") authToken: String,
                        @Query("invitation_id") invitationID: String):
            Observable<Response<AddFriendResponseJson>>

    @GET("/v1/invitations/{invitation_id}/friend")
    fun getInvitedFriend(@Path("invitation_id") invitationID: String):
            Observable<Response<InvitedFriendJson>>

    @DELETE("/v1/friends/{friend_id}")
    fun deleteFriend(@Header("Authorization") authToken: String, @Path("friend_id") invitationID: String):
            Completable

    @PATCH("/v1/user/profile")
    fun getSaveProfileResponse(@Header("Authorization") authToken: String,
                               @Body requestSave: SaveProfileRequest):
            Observable<Response<SaveProfileResponse>>

    @PATCH("/v1/user/profile")
    fun getSaveProfileWithoutImageResponse(@Header("Authorization") authToken: String,
                                           @Query("first_name") firstName: String,
                                           @Query("last_name") lastName: String
    ): Observable<Response<SaveProfileResponse>>

    @GET("/v1/user/profile")
    fun getProfileResponse(@Header("Authorization") authToken: String):
            Observable<Response<GetProfileResponse>>

    @GET("/v1/facilities")
    fun getFacilities(@Header("Authorization") authToken: String):
            Observable<Response<FacilitiesResponse>>
}
