package com.example.asinahaberuygulamasi.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.asinahaberuygulamasi.RetrofitClient
import com.example.asinahaberuygulamasi.model.KullaniciKayitRequest
import com.example.asinahaberuygulamasi.ui.theme.BundleDarkBg
import com.example.asinahaberuygulamasi.ui.theme.BundleSurface
import com.example.asinahaberuygulamasi.ui.theme.BundleTextSecondary
import kotlinx.coroutines.launch

@Composable
fun LoginScreen(
    onLoginSuccess: (String, Boolean) -> Unit,
    onRegisterClick: () -> Unit,
    onBackClick: () -> Unit
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isimField by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var kayitModu by remember { mutableStateOf(false) }

    val scope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BundleDarkBg)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "AŞİNA",
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.ExtraBold,
            color = Color.White,
            letterSpacing = 4.sp
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Haberin Yeni Yüzü",
            style = MaterialTheme.typography.bodyMedium,
            color = BundleTextSecondary
        )

        Spacer(modifier = Modifier.height(48.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = BundleSurface)
        ) {
            Column(modifier = Modifier.padding(24.dp)) {
                Text(
                    text = if (kayitModu) "Kayıt Ol" else "Giriş Yap",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp
                )

                Spacer(modifier = Modifier.height(24.dp))

                if (kayitModu) {
                    OutlinedTextField(
                        value = isimField,
                        onValueChange = { isimField = it; errorMessage = "" },
                        label = { Text("İsim", color = BundleTextSecondary) },
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedBorderColor = Color(0xFF2D7FF9),
                            unfocusedBorderColor = BundleTextSecondary
                        ),
                        shape = RoundedCornerShape(12.dp)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                }

                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it; errorMessage = "" },
                    label = { Text("E-posta", color = BundleTextSecondary) },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedBorderColor = Color(0xFF2D7FF9),
                        unfocusedBorderColor = BundleTextSecondary
                    ),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                    shape = RoundedCornerShape(12.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it; errorMessage = "" },
                    label = { Text("Şifre", color = BundleTextSecondary) },
                    modifier = Modifier.fillMaxWidth(),
                    visualTransformation = PasswordVisualTransformation(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedBorderColor = Color(0xFF2D7FF9),
                        unfocusedBorderColor = BundleTextSecondary
                    ),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    shape = RoundedCornerShape(12.dp)
                )

                if (errorMessage.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = errorMessage, color = Color.Red, fontSize = 13.sp)
                }

                Spacer(modifier = Modifier.height(32.dp))

                Button(
                    onClick = {
                        if (email.isBlank() || password.isBlank()) {
                            errorMessage = "E-posta ve şifre boş bırakılamaz."
                            return@Button
                        }

                        scope.launch {
                            isLoading = true
                            errorMessage = ""
                            try {
                                if (kayitModu) {
                                    // KAYIT
                                    RetrofitClient.apiService.kayitOl(
                                        KullaniciKayitRequest(
                                            email = email,
                                            sifre = password,
                                            isim = isimField
                                        )
                                    )
                                    // Kayıt başarılı, giriş moduna geç
                                    kayitModu = false
                                    errorMessage = "Kayıt başarılı! Şimdi giriş yapabilirsiniz."
                                } else {
                                    // GİRİŞ
                                    val sonuc = RetrofitClient.apiService.girisYap(
                                        KullaniciKayitRequest(
                                            email = email,
                                            sifre = password
                                        )
                                    )
                                    onLoginSuccess(sonuc.email, sonuc.admin)
                                }
                            } catch (e: retrofit2.HttpException) {
                                errorMessage = when (e.code()) {
                                    400 -> "Bu email zaten kayıtlı."
                                    401 -> "Email veya şifre hatalı."
                                    else -> "Bir hata oluştu: ${e.code()}"
                                }
                            } catch (e: Exception) {
                                errorMessage = "Sunucuya bağlanılamadı."
                            }
                            isLoading = false
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2D7FF9)),
                    shape = RoundedCornerShape(12.dp),
                    enabled = !isLoading
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                    } else {
                        Text(
                            text = if (kayitModu) "KAYIT OL" else "GİRİŞ YAP",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                TextButton(
                    onClick = {
                        kayitModu = !kayitModu
                        errorMessage = ""
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = if (kayitModu) "Zaten hesabınız var mı? Giriş Yapın"
                        else "Hesabınız yok mu? Kayıt Olun",
                        color = BundleTextSecondary
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        TextButton(onClick = onBackClick) {
            Text("Üye Olmadan Devam Et", color = BundleTextSecondary)
        }
    }
}