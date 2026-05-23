using System.Security.Cryptography;
using System.Text;

namespace BackEnd_Destino.Helpers;

public static class EncryptionHelper
{
    private static readonly string Key = Environment.GetEnvironmentVariable("ENCRYPTION_KEY") ?? "DefaultKeyMustBe32CharsLong123456";

    public static string Encrypt(string plainText)
    {
        if (string.IsNullOrEmpty(plainText)) return plainText;

        byte[] iv = new byte[16];
        byte[] array;

        using (Aes aes = Aes.Create())
        {
            aes.Key = Encoding.UTF8.GetBytes(Key.PadRight(32).Substring(0, 32));
            using (RandomNumberGenerator rng = RandomNumberGenerator.Create())
            {
                rng.GetBytes(iv);
            }
            aes.IV = iv;

            ICryptoTransform encryptor = aes.CreateEncryptor(aes.Key, aes.IV);

            using (MemoryStream memoryStream = new MemoryStream())
            {
                // Write the IV to the beginning of the stream
                memoryStream.Write(iv, 0, iv.Length);

                using (CryptoStream cryptoStream = new CryptoStream(memoryStream, encryptor, CryptoStreamMode.Write))
                {
                    using (StreamWriter streamWriter = new StreamWriter(cryptoStream))
                    {
                        streamWriter.Write(plainText);
                    }
                }
                
                array = memoryStream.ToArray();
            }
        }

        return Convert.ToBase64String(array);
    }

    public static string Decrypt(string cipherText)
    {
        if (string.IsNullOrEmpty(cipherText)) return cipherText;

        byte[] buffer = Convert.FromBase64String(cipherText);

        if (buffer.Length < 16) return cipherText; // Invalid format

        byte[] iv = new byte[16];
        Array.Copy(buffer, 0, iv, 0, iv.Length);

        using (Aes aes = Aes.Create())
        {
            aes.Key = Encoding.UTF8.GetBytes(Key.PadRight(32).Substring(0, 32));
            aes.IV = iv;
            ICryptoTransform decryptor = aes.CreateDecryptor(aes.Key, aes.IV);

            using (MemoryStream memoryStream = new MemoryStream(buffer, iv.Length, buffer.Length - iv.Length))
            {
                using (CryptoStream cryptoStream = new CryptoStream(memoryStream, decryptor, CryptoStreamMode.Read))
                {
                    using (StreamReader streamReader = new StreamReader(cryptoStream))
                    {
                        return streamReader.ReadToEnd();
                    }
                }
            }
        }
    }
}
