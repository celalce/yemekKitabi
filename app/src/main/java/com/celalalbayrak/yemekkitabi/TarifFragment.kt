package com.celalalbayrak.yemekkitabi

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.celalalbayrak.yemekkitabi.databinding.FragmentListeBinding
import com.celalalbayrak.yemekkitabi.databinding.FragmentTarifBinding
import com.google.android.material.snackbar.Snackbar

class TarifFragment : Fragment() {
    private var _binding: FragmentTarifBinding? = null
    private val binding get() = _binding!!
    private lateinit var permissionLauncher: ActivityResultLauncher<String>
    private lateinit var activityResultLauncher: ActivityResultLauncher<Intent>
    private var secilenGorsel : Uri? = null
    private var secilenBitmap : Bitmap? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        registerLauncher()

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentTarifBinding.inflate(inflater, container, false)
        val view = binding.root
        return view
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.imageView.setOnClickListener { gorselSec(it) }
        binding.kaydetButton.setOnClickListener { kaydet(it) }
        binding.silButton.setOnClickListener { sil(it) }

        arguments?.let {
             val bilgi = TarifFragmentArgs.fromBundle(it).bilgi
             if(bilgi == "yeni"){
                 //yeni tarif eklenecek
                 binding.silButton.isEnabled = false
                 binding.kaydetButton.isEnabled = true
                binding.isimText.setText("")
                 binding.malzemeText.setText("")


            }else{
                binding.silButton.isEnabled = true
                binding.kaydetButton.isEnabled = false
             }
        }

    }



    fun kaydet(view: View) {

    }
    fun sil(view: View){

    }
    fun gorselSec(view: View){

       if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU){

           if(ContextCompat.checkSelfPermission(requireContext(), android.Manifest.permission.READ_MEDIA_IMAGES) != PackageManager.PERMISSION_GRANTED){
               //!= PackageManager.PERMISSION_GRANTED)  : anlama eger izin verilmediyse demektir.
               // izin istenmemiş, izin istememiz gerekiyor.
               if (ActivityCompat.shouldShowRequestPermissionRationale(requireActivity(), android.Manifest.permission.READ_MEDIA_IMAGES)){
                   //Snackbar göstermemiz lazım, kullanıcıdan neden izin istediğimizi bir kez daha söyleyerek izin istememiz gerekir.
                   Snackbar.make(view,"Galeriye ulaşıp görsel seçmemiz lazım",Snackbar.LENGTH_INDEFINITE).setAction(
                       "İzin Ver",
                       View.OnClickListener {
                           // izin isteyeceğiz
                           permissionLauncher.launch(android.Manifest.permission.READ_MEDIA_IMAGES)

                       }
                   ).show()
               }else{
                   //izin isteyeceğiz
                   permissionLauncher.launch(android.Manifest.permission.READ_MEDIA_IMAGES)
               }


           }else{


               // izin verilmiş, galeriye gidebiliriz

               val intentGallery = Intent(Intent.ACTION_PICK,MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
               activityResultLauncher.launch(intentGallery)
           }

       }else{
           if(ContextCompat.checkSelfPermission(requireContext(), android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
               //!= PackageManager.PERMISSION_GRANTED)  : anlama eger izin verilmediyse demektir.
               // izin istenmemiş, izin istememiz gerekiyor.
               if (ActivityCompat.shouldShowRequestPermissionRationale(requireActivity(), android.Manifest.permission.READ_EXTERNAL_STORAGE)){
                   //Snackbar göstermemiz lazım, kullanıcıdan neden izin istediğimizi bir kez daha söyleyerek izin istememiz gerekir.
                   Snackbar.make(view,"Galeriye ulaşıp görsel seçmemiz lazım",Snackbar.LENGTH_INDEFINITE).setAction(
                       "İzin Ver",
                       View.OnClickListener {
                           // izin isteyeceğiz
                           permissionLauncher.launch(android.Manifest.permission.READ_EXTERNAL_STORAGE)

                       }
                   ).show()
               }else{
                   //izin isteyeceğiz
                   permissionLauncher.launch(android.Manifest.permission.READ_EXTERNAL_STORAGE)
               }


           }else{


               // izin verilmiş, galeriye gidebiliriz

               val intentGallery = Intent(Intent.ACTION_PICK,MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
               activityResultLauncher.launch(intentGallery)
           }

       }


    }

    private  fun registerLauncher(){
        activityResultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){result->
            if (result.resultCode == Activity.RESULT_OK){
                val intentFromResult = result.data
                if (intentFromResult != null){
                    secilenGorsel = intentFromResult.data
                    try {
                        if (Build.VERSION.SDK_INT >= 28){
                            val source = ImageDecoder.createSource(requireActivity().contentResolver,secilenGorsel!!)
                            secilenBitmap = ImageDecoder.decodeBitmap(source)
                            binding.imageView.setImageBitmap(secilenBitmap)
                        }else{
                            secilenBitmap = MediaStore.Images.Media.getBitmap(activity?.contentResolver, secilenGorsel)
                            binding.imageView.setImageBitmap(secilenBitmap)
                        }
                    }catch (e: Exception){
                       println(e.localizedMessage)
                    }





                }
            }
        }


        permissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) { result->
            if (result){
                //izin verildi
                //Galeriye gidebiliriz
                val intentGallery = Intent(Intent.ACTION_PICK,MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                activityResultLauncher.launch(intentGallery)

            }
        }

    }





    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


}