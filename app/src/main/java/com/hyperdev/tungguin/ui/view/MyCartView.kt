package com.hyperdev.tungguin.ui.view

import com.hyperdev.tungguin.model.cart.CartData
import com.hyperdev.tungguin.model.cart.CartItem
import com.hyperdev.tungguin.model.cart.CheckoutData
import com.hyperdev.tungguin.model.cart.DataVoucher

class MyCartView {

    interface View {

        // Menampilkan list item dari produk (desain) yang dipesan
        fun showCartItem(cartItem: List<CartItem>)

        // Menampilkan rincian produk yang di order
        fun showCartData(cartData: CartData)

        // Menampilkan ProgressBar saat data request
        fun showProgressBar()

        // Untuk menghilangkan ProgressBar
        fun hideProgressBar()

        // Untuk mendapatkan data dari kode voucher yang telah di cek
        fun getVoucherData(data: DataVoucher)

        // Untuk mendapatkan data transaksi saat proses pembayaran di lakukan
        fun getCheckoutData(data: CheckoutData)

        // Untuk menampilkan jumlah item dari produk (desain) yang dipesan
        fun showItemCount(count: String)

        // Membuat statement jika permintaan untuk memuat data berhasil
        fun onSuccessLoadData()

        // Membuat statement jika permintaan untuk menghapus item berhasil
        fun onSuccessDeleteItem()

        // Membuat statement jika permintaan untuk mengecek voucher berhasil
        fun onSuccessCheckVoucher()

        // Membuat statement jika permintaan untuk checkout / pembayaran berhasil
        fun onSuccessCheckout()

        // Method untuk menghandle jika tidak ada koneksi internet
        fun noInternetConnection(message: String)
    }

    interface Presenter {

        // Untuk Mendapatkan data dari keranjang belanja
        fun getCartData(token: String)

        // Untuk mengecek ketersediaan kode voucher
        fun checkVoucher(token: String, accept: String, code: String)

        // Untuk melakukan proses pembayaran melalui wallet atau midtrans
        fun checkout(token: String, accept: String, paymentType: String, voucher: String)

        // Untuk menghentikan proses request ke server dan mengembalikan keadaan menjadi seperti semula
        fun onDestroy()
    }
}