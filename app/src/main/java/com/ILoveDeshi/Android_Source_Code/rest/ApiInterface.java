package com.ILoveDeshi.Android_Source_Code.rest;

import com.ILoveDeshi.Android_Source_Code.BuildConfig;
import com.ILoveDeshi.Android_Source_Code.response.AVStatusRP;
import com.ILoveDeshi.Android_Source_Code.response.AboutUsRP;
import com.ILoveDeshi.Android_Source_Code.response.AccountDetailRP;
import com.ILoveDeshi.Android_Source_Code.response.AppRP;
import com.ILoveDeshi.Android_Source_Code.response.AppsRP;
import com.ILoveDeshi.Android_Source_Code.response.AppsRatingRP;
import com.ILoveDeshi.Android_Source_Code.response.CheckOtpRP;
import com.ILoveDeshi.Android_Source_Code.response.ContactRP;
import com.ILoveDeshi.Android_Source_Code.response.DataRP;
import com.ILoveDeshi.Android_Source_Code.response.FaqRP;
import com.ILoveDeshi.Android_Source_Code.response.GameRP;
import com.ILoveDeshi.Android_Source_Code.response.HomeRP;
import com.ILoveDeshi.Android_Source_Code.response.HomeTopUserRP;
import com.ILoveDeshi.Android_Source_Code.response.LanguageRP;
import com.ILoveDeshi.Android_Source_Code.response.LoginRP;
import com.ILoveDeshi.Android_Source_Code.response.MostAppsRP;
import com.ILoveDeshi.Android_Source_Code.response.PackageRP;
import com.ILoveDeshi.Android_Source_Code.response.PackageTaskRP;
import com.ILoveDeshi.Android_Source_Code.response.PackageTransRP;
import com.ILoveDeshi.Android_Source_Code.response.PaymentModeRP;
import com.ILoveDeshi.Android_Source_Code.response.PlayAdRP;
import com.ILoveDeshi.Android_Source_Code.response.PointDetailRP;
import com.ILoveDeshi.Android_Source_Code.response.PrivacyPolicyRP;
import com.ILoveDeshi.Android_Source_Code.response.ProductsRP;
import com.ILoveDeshi.Android_Source_Code.response.ProfileRP;
import com.ILoveDeshi.Android_Source_Code.response.ProfileStatusRP;
import com.ILoveDeshi.Android_Source_Code.response.RegisterRP;
import com.ILoveDeshi.Android_Source_Code.response.RewardPointRP;
import com.ILoveDeshi.Android_Source_Code.response.SearchRP;
import com.ILoveDeshi.Android_Source_Code.response.SingleAppRP;
import com.ILoveDeshi.Android_Source_Code.response.SingleRatingAppRP;
import com.ILoveDeshi.Android_Source_Code.response.SpinnerRP;
import com.ILoveDeshi.Android_Source_Code.response.StatusDownloadRP;
import com.ILoveDeshi.Android_Source_Code.response.SubmitAdPlayRP;
import com.ILoveDeshi.Android_Source_Code.response.SubmitSecureWorldRP;
import com.ILoveDeshi.Android_Source_Code.response.SubmitSpinnerRP;
import com.ILoveDeshi.Android_Source_Code.response.SubscribeRP;
import com.ILoveDeshi.Android_Source_Code.response.SuspendRP;
import com.ILoveDeshi.Android_Source_Code.response.TopUserRP;
import com.ILoveDeshi.Android_Source_Code.response.TransactionDetailRP;
import com.ILoveDeshi.Android_Source_Code.response.URPListRP;
import com.ILoveDeshi.Android_Source_Code.response.UserFollowRP;
import com.ILoveDeshi.Android_Source_Code.response.UserFollowStatusRP;
import com.ILoveDeshi.Android_Source_Code.response.UserRedeemRP;
import com.ILoveDeshi.Android_Source_Code.response.VideoRP;
import com.ILoveDeshi.Android_Source_Code.response.WebAppRP;
import com.ILoveDeshi.Android_Source_Code.response.WebsiteRP;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface ApiInterface {

    //get app data
    @POST(BuildConfig.API_FILE)
    @FormUrlEncoded
    Call<AppRP> getAppData(@Field("data") String data);

    //login
    @POST(BuildConfig.API_FILE)
    @FormUrlEncoded
    Call<LoginRP> getLogin(@Field("data") String data);

    //login check
    @POST(BuildConfig.API_FILE)
    @FormUrlEncoded
    Call<LoginRP> getLoginDetail(@Field("data") String data);

    //check otp on/off
    @POST(BuildConfig.API_FILE)
    @FormUrlEncoded
    Call<CheckOtpRP> getOtpStatus(@Field("data") String data);

    //send otp verification email
    @POST(BuildConfig.API_FILE)
    @FormUrlEncoded
    Call<DataRP> getVerification(@Field("data") String data);

    //register
    @POST(BuildConfig.API_FILE)
    @FormUrlEncoded
    Call<RegisterRP> getRegisterDetail(@Field("data") String data);

    //submit reference code
    @POST(BuildConfig.API_FILE)
    @FormUrlEncoded
    Call<DataRP> submitReferenceCode(@Field("data") String data);

    //forget password
    @POST(BuildConfig.API_FILE)
    @FormUrlEncoded
    Call<DataRP> getForgetPassword(@Field("data") String data);

    //profile
    @POST(BuildConfig.API_FILE)
    @FormUrlEncoded
    Call<ProfileRP> getProfile(@Field("data") String data);

    //edit profile
    @POST(BuildConfig.API_FILE)
    @Multipart
    Call<DataRP> getEditProfile(@Part("data") RequestBody data, @Part MultipartBody.Part part);

    //update password
    @POST(BuildConfig.API_FILE)
    @FormUrlEncoded
    Call<DataRP> updatePassword(@Field("data") String data);

    //get language
    @POST(BuildConfig.API_FILE)
    @FormUrlEncoded
    Call<LanguageRP> getLanguage(@Field("data") String data);

    //home page
    @POST(BuildConfig.API_FILE)
    @FormUrlEncoded
    Call<HomeRP> getHome(@Field("data") String data);

    @POST(BuildConfig.API_FILE)
    @FormUrlEncoded
    Call<PackageRP> getPackageList(@Field("data") String data);

    @POST(BuildConfig.API_FILE)
    @FormUrlEncoded
    Call<HomeTopUserRP> getTopThree(@Field("data") String data);

    //home page
    @POST(BuildConfig.API_FILE)
    @FormUrlEncoded
    Call<TopUserRP> getTopUser(@Field("data") String data);

    //apps
    @POST(BuildConfig.API_FILE)
    @FormUrlEncoded
    Call<AppsRP> getApps(@Field("data") String data);

    @POST(BuildConfig.API_FILE)
    @FormUrlEncoded
    Call<PackageTaskRP> getTaskList(@Field("data") String data);

    //most apps
    @POST(BuildConfig.API_FILE)
    @FormUrlEncoded
    Call<MostAppsRP> getMostApps(@Field("data") String data);

    @POST(BuildConfig.API_FILE)
    @FormUrlEncoded
    Call<ProductsRP> getProducts(@Field("data") String data);

    @POST(BuildConfig.API_FILE)
    @FormUrlEncoded
    Call<AppsRatingRP> getRatingApps(@Field("data") String data);

    //featured website
    @POST(BuildConfig.API_FILE)
    @FormUrlEncoded
    Call<WebsiteRP> getFeaturedWeb(@Field("data") String data);

    //video
    @POST(BuildConfig.API_FILE)
    @FormUrlEncoded
    Call<VideoRP> getVideoList(@Field("data") String data);

    //status view
    @POST(BuildConfig.API_FILE)
    @FormUrlEncoded
    Call<DataRP> statusView(@Field("data") String data);

    //status download
    @POST(BuildConfig.API_FILE)
    @FormUrlEncoded
    Call<StatusDownloadRP> statusDownloadCount(@Field("data") String data);

    //submit review
    @POST(BuildConfig.API_FILE)
    @FormUrlEncoded
    Call<DataRP> submitReview(@Field("data") String data);

    //get user reference code
    @POST(BuildConfig.API_FILE)
    @FormUrlEncoded
    Call<ProfileRP> getUserReferenceCode(@Field("data") String data);

    @POST(BuildConfig.API_FILE)
    @FormUrlEncoded
    Call<PackageTransRP> getPackageTrans(@Field("data") String data);

    //user follow and unfollow
    @POST(BuildConfig.API_FILE)
    @FormUrlEncoded
    Call<UserFollowStatusRP> getUserFollowStatus(@Field("data") String data);

    //get user follower, following and user search list
    @POST(BuildConfig.API_FILE)
    @FormUrlEncoded
    Call<UserFollowRP> getUserFollow(@Field("data") String data);

    //user status delete
    @POST(BuildConfig.API_FILE)
    @FormUrlEncoded
    Call<DataRP> deleteStatus(@Field("data") String data);

    //get user daily upload limit
    @POST(BuildConfig.API_FILE)
    @FormUrlEncoded
    Call<DataRP> getDailyUploadLimit(@Field("data") String data);

    //upload quotes
    @POST(BuildConfig.API_FILE)
    @FormUrlEncoded
    Call<DataRP> uploadQuotes(@Field("data") String data);

    //user profile status check
    @POST(BuildConfig.API_FILE)
    @FormUrlEncoded
    Call<ProfileStatusRP> getProfileStatus(@Field("data") String data);

    //account verification request
    @POST(BuildConfig.API_FILE)
    @Multipart
    Call<DataRP> submitAccountVerification(@Part("data") RequestBody data, @Part MultipartBody.Part part);

    //account verification status
    @POST(BuildConfig.API_FILE)
    @FormUrlEncoded
    Call<AVStatusRP> getAVStatus(@Field("data") String data);

    //account detail
    @POST(BuildConfig.API_FILE)
    @FormUrlEncoded
    Call<AccountDetailRP> getAccountDetail(@Field("data") String data);

    //delete account
    @POST(BuildConfig.API_FILE)
    @FormUrlEncoded
    Call<DataRP> deleteAccount(@Field("data") String data);

    //get game list
    @POST(BuildConfig.API_FILE)
    @FormUrlEncoded
    Call<GameRP> getGameList(@Field("data") String data);

    //get subs list
    @POST(BuildConfig.API_FILE)
    @FormUrlEncoded
    Call<SubscribeRP> getSubscriber(@Field("data") String data);

    //user account suspend
    @POST(BuildConfig.API_FILE)
    @FormUrlEncoded
    Call<SuspendRP> getSuspend(@Field("data") String data);

    //get spinner data
    @POST(BuildConfig.API_FILE)
    @FormUrlEncoded
    Call<SpinnerRP> getSpinnerData(@Field("data") String data);

    @POST(BuildConfig.API_FILE)
    @FormUrlEncoded
    Call<PlayAdRP> getPlayAdData(@Field("data") String data);

    //get app data
    @POST(BuildConfig.API_FILE)
    @FormUrlEncoded
    Call<SingleAppRP> getSingleApp(@Field("data") String data);

    @POST(BuildConfig.API_FILE)
    @FormUrlEncoded
    Call<SingleRatingAppRP> getSingleRatingApp(@Field("data") String data);

    //get search data
    @POST(BuildConfig.API_FILE)
    @FormUrlEncoded
    Call<SearchRP> getSearch(@Field("data") String data);

    //get web data
    @POST(BuildConfig.API_FILE)
    @FormUrlEncoded
    Call<WebAppRP> getWeb(@Field("data") String data);

    //submit spinner data
    @POST(BuildConfig.API_FILE)
    @FormUrlEncoded
    Call<SubmitSpinnerRP> submitSpinnerData(@Field("data") String data);

    @POST(BuildConfig.API_FILE)
    @FormUrlEncoded
    Call<SubmitAdPlayRP> submitPlayAdData(@Field("data") String data);

    //submit spinner data
    @POST(BuildConfig.API_FILE)
    @FormUrlEncoded
    Call<SubmitSecureWorldRP> submitSecureWorld(@Field("data") String data);

    //get payment mode
    @POST(BuildConfig.API_FILE)
    @FormUrlEncoded
    Call<PaymentModeRP> getPaymentMode(@Field("data") String data);

    //submit payment detail
    @POST(BuildConfig.API_FILE)
    @FormUrlEncoded
    Call<DataRP> submitPaymentDetail(@Field("data") String data);

    //app point detail
    @POST(BuildConfig.API_FILE)
    @FormUrlEncoded
    Call<PointDetailRP> getPointDetail(@Field("data") String data);

    //get reward point
    @POST(BuildConfig.API_FILE)
    @FormUrlEncoded
    Call<RewardPointRP> getUserRewardPoint(@Field("data") String data);

    //get user reward point list
    @POST(BuildConfig.API_FILE)
    @FormUrlEncoded
    Call<URPListRP> getUserRewardPointList(@Field("data") String data);

    //get user redeem point history
    @POST(BuildConfig.API_FILE)
    @FormUrlEncoded
    Call<UserRedeemRP> getUserRedeemHistory(@Field("data") String data);

    //get user reward point history
    @POST(BuildConfig.API_FILE)
    @FormUrlEncoded
    Call<URPListRP> getURPointHistoryList(@Field("data") String data);

    //transaction detail
    @POST(BuildConfig.API_FILE)
    @FormUrlEncoded
    Call<TransactionDetailRP> getTransactionDetail(@Field("data") String data);

    //get about us
    @POST(BuildConfig.API_FILE)
    @FormUrlEncoded
    Call<AboutUsRP> getAboutUs(@Field("data") String data);

    //get privacy policy
    @POST(BuildConfig.API_FILE)
    @FormUrlEncoded
    Call<PrivacyPolicyRP> getPrivacyPolicy(@Field("data") String data);

    //get faq
    @POST(BuildConfig.API_FILE)
    @FormUrlEncoded
    Call<FaqRP> getFaq(@Field("data") String data);

    //get contact us list
    @POST(BuildConfig.API_FILE)
    @FormUrlEncoded
    Call<ContactRP> getContactSub(@Field("data") String data);

    //Submit contact
    @POST(BuildConfig.API_FILE)
    @FormUrlEncoded
    Call<DataRP> submitContact(@Field("data") String data);

}
