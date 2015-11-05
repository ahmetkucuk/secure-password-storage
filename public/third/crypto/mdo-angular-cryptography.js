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

                    return CryptoJS.AES.encrypt(message, key ).toString();
                },

                decrypt: function(message, key) {

                    if (key === undefined) {
                        key = cryptoKey;
                    }

                    //var wordArray = CryptoJS.enc.Utf8.parse(key);
                    //var base64Key = CryptoJS.enc.Base64.stringify(wordArray);
                    //var key = CryptoJS.enc.Base64.parse(base64Key);
                    var decryptedData = CryptoJS.AES.decrypt(CryptoJS.enc.Base64.parse(message), key, {
                        mode: CryptoJS.mode.ECB,
                        padding: CryptoJS.pad.Pkcs7
                    });

                    var decryptedText = decryptedData.toString( CryptoJS.enc.Utf8 );
                    return decryptedText;
                }
            }
        }];
    });