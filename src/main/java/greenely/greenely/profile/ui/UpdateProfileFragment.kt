package greenely.greenely.profile.ui

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.os.Handler
import android.text.TextUtils
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import coil.api.load
import coil.transform.CircleCropTransformation
import com.github.dhaval2404.imagepicker.ImagePicker
import com.google.android.material.snackbar.Snackbar
import dagger.android.support.AndroidSupportInjection
import greenely.greenely.R
import greenely.greenely.api.models.ErrorMessage
import greenely.greenely.databinding.FragmentUpdateProfileBinding
import greenely.greenely.databinding.PasswordChangeErrorDialogBinding
import greenely.greenely.models.Resource
import greenely.greenely.profile.json.SaveProfileRequest
import greenely.greenely.profile.json.SaveProfileResponse
import greenely.greenely.profile.model.Account
import greenely.greenely.store.UserStore
import greenely.greenely.tracking.Tracker
import greenely.greenely.tracking.TrackerFactory
import greenely.greenely.utils.BitmapToBase64Converter
import java.io.File
import javax.inject.Inject
import retrofit2.HttpException
import java.io.IOException

class UpdateProfileFragment : Fragment() {

    @Inject
    internal lateinit var factory: ViewModelProvider.Factory

    @Inject
    internal lateinit var tracker: Tracker

    @Inject
    internal lateinit var bitmapToBase64Converter: BitmapToBase64Converter


    private lateinit var viewModel: UpdateProfileViewModel

    private lateinit var binding: FragmentUpdateProfileBinding

    private var profileAvatar: File? = null

    @Inject
    lateinit var userStore: UserStore


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(activity!!, factory).get(UpdateProfileViewModel::class.java)
        handleClickListeners()
        bindViews()
    }

    override fun onResume() {
        super.onResume()
        tracker.trackScreen("Settings Update Profile")
    }

    /**
     * Inject before attaching to the [context].
     */
    override fun onAttach(context: Context) {
        AndroidSupportInjection.inject(this)
        super.onAttach(context)
    }


    private fun showErrorMessage(error: Throwable) {
        when (error) {
            is HttpException -> {
                ErrorMessage.fromError(error)?.let { errorMessage ->
                    Snackbar.make(binding.container, errorMessage.description, Snackbar.LENGTH_LONG).show()
                }
            }
            is IOException -> {
                Snackbar.make(binding.container, R.string.network_error_body, Snackbar.LENGTH_LONG)
                        .show()
            }
            else -> {
                Snackbar.make(binding.container, R.string.unexpected_error, Snackbar.LENGTH_LONG)
                        .show()
            }
        }
    }

    /**
     * Create the view and the binding.
     */
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_update_profile, container, false)
        return binding.root
    }


    @SuppressLint("SetTextI18n")
    private fun bindViews() {

        if (!userStore.firstName.equals("")) {
            binding.profileFirstName.setText(userStore.firstName)
            binding.updateProfileTitle.text = userStore.firstName + resources.getString(R.string.updateProfileFragment_title)
        }
        if (!userStore.lastName.equals(""))
            binding.profileLastName.setText(userStore.lastName)
        if (!userStore.avatar.equals(""))
            binding.profileUserImage.load(userStore.avatar)
            {
                crossfade(true)
                transformations(CircleCropTransformation())
            }


        viewModel.profileSaveResponseLiveData.observe(this, Observer {
            when (it) {
                is Resource.Success -> {
                    binding.loader.hide()
                    Snackbar.make(binding.container, "Din profil har sparats", Snackbar.LENGTH_LONG).show()
                    userStore.firstName = it.value.firstName
                    userStore.lastName = it.value.lastName
                    userStore.avatar = it.value.avatarUrl
                    //updateAccountsMap(it.value)
                    Handler().postDelayed({
                        activity?.onBackPressed()
                    }, 500)
                }
                is Resource.Loading -> {
                    binding.loader.show()
                }

                is Resource.Error -> {
                    binding.loader.hide()
                    showErrorMessage(it.error)
                    disableViews(false)
                }
            }
        })
    }

    private fun updateAccountsMap(saveProfileResponse: SaveProfileResponse) {
        val accountMap = userStore.getAccountsMapFromPreference()
        if (accountMap.containsKey(userStore.userId)) {
            val account: Account? = accountMap[userStore.userId]
            account?.name = saveProfileResponse.firstName + " " + saveProfileResponse.lastName
            account?.let { it1 -> userStore.userId?.let { accountMap.put(it, it1) } }
            userStore.storeAccountsMapToPreference(accountMap)
        }
    }

    private fun validateNames(): Boolean {
        if (TextUtils.isEmpty(binding.profileFirstName.text))
            return false
        if (TextUtils.isEmpty(binding.profileLastName.text))
            return false
        return true
    }

    private fun handleClickListeners() {

        binding.profileUserImage.setOnClickListener {
            ImagePicker.with(this)
                    .crop()
                    .galleryOnly()
                    .compress(1024)
                    .maxResultSize(1080, 1080)
                    .start { resultCode, data ->
                        when (resultCode) {
                            Activity.RESULT_OK -> {
                                val fileUri = data?.data
                                profileAvatar = ImagePicker.getFile(data)
                                binding.profileUserImage.load(fileUri) {
                                    crossfade(true)
                                    placeholder(R.drawable.ic_circular_placeholder)
                                    transformations(CircleCropTransformation())
                                }
                            }

                            ImagePicker.RESULT_ERROR -> {
                                Toast.makeText(context, ImagePicker.getError(data), Toast.LENGTH_SHORT).show()
                            }

                            else -> {
                                // Toast.makeText(context, "Task Cancelled", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
        }


        binding.btnSave.setOnClickListener {
            if (validateNames()) {
                disableViews(true)
                if (profileAvatar != null) {
                    val bitmap = BitmapFactory.decodeFile(profileAvatar?.absolutePath.toString())
                    val saveProfileRequest = SaveProfileRequest()
                    saveProfileRequest.firstName = binding.profileFirstName.text.toString()
                    saveProfileRequest.lastName = binding.profileLastName.text.toString()
                    saveProfileRequest.avatar = bitmapToBase64Converter.toBase64(bitmap)
                    viewModel.getProfileSaveResponse(saveProfileRequest)
                } else {
                    if (!userStore.firstName?.equals(binding.profileFirstName.text.toString().trim())!! || !userStore.lastName?.equals(binding.profileLastName.text.toString().trim())!!)
                        viewModel.getProfileSaveResponse(binding.profileFirstName.text.toString(), binding.profileLastName.text.toString())
                    else
                        disableViews(false)
                }
            } else {
                Snackbar.make(binding.container, "Vänligen fyll i fälten", Snackbar.LENGTH_LONG).show()
            }
        }


        binding.toolbar.setNavigationOnClickListener {
            activity?.onBackPressed()
        }
    }

    private fun disableViews(boolean: Boolean) {
        if (boolean) {
            binding.profileFirstName.setTextColor(resources.getColor(R.color.grey3))
            binding.profileLastName.setTextColor(resources.getColor(R.color.grey3))
            binding.profileFirstName.isEnabled = false
            binding.profileLastName.isEnabled = false
            binding.profileUserImage.isEnabled = false
        } else {
            binding.profileFirstName.setTextColor(resources.getColor(R.color.jetBlack))
            binding.profileLastName.setTextColor(resources.getColor(R.color.jetBlack))
            binding.profileFirstName.isEnabled = true
            binding.profileLastName.isEnabled = true
            binding.profileUserImage.isEnabled = true
        }
    }

}