angular.module('mdo-angular-cryptography', [])
    .provider('$crypto', function CryptoKeyProvider() {
        var cryptoKey;

        this.setCryptographyKey = function(value) {
            cryptoKey = value;
        };

        this.$get = [function(){
            return {
                getCryptoKey: function() {
                    return cryptoKey
                },

                encrypt: function(message, key) {

                    if (key === undefined) {
                        key = cryptoKey;
                    }
                    var encryptedData = CryptoJS.AES.encrypt(message, key, {
                        mode: CryptoJS.mode.ECB,
                        padding: CryptoJS.pad.Pkcs7
                    });
                    return encryptedData;
                },

                decrypt: function(message, key) {

                    if (key === undefined) {
                        key = cryptoKey;
                    }
                    var words = CryptoJS.enc.Utf8.parse(key); // WordArray object
                    var base64Key = CryptoJS.enc.Base64.stringify(words);

                    //var wordArray = CryptoJS.enc.Utf8.parse(key);
                    //var base64Key = CryptoJS.enc.Base64.stringify(wordArray);
                    key = CryptoJS.enc.Base64.parse(base64Key);
                    var decryptedData = CryptoJS.AES.decrypt(message, key, {
                        mode: CryptoJS.mode.ECB,
                        padding: CryptoJS.pad.Pkcs7
                    });

                    var decryptedText = decryptedData.toString( CryptoJS.enc.Utf8 );
                    return decryptedText;
                }
            }
        }];
    });