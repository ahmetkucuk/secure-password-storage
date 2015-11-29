var app = angular.module("app", ["ngResource", "ngRoute", "mdo-angular-cryptography", "ngProgress"])
    .constant("apiUrl", "http://127.0.0.1:9000/api/")
    .config(["$routeProvider", function($routeProvider) {
        return $routeProvider.when("/", {
            templateUrl: "/assets/html/home-list.html",
            controller: "HomeCtrl"
        }).when("/home", {
            templateUrl: "/assets/html/home-list.html",
            controller: "HomeCtrl"
        }).when("/login", {
            templateUrl: "/assets/html/login.html",
            controller: "LoginCtrl"
        }).when("/register", {
            templateUrl: "/assets/html/register.html",
            controller: "RegisterCtrl"
        }).otherwise({
            redirectTo: "/"
        });
    }
    ]).config([
        "$locationProvider", function($locationProvider) {
            return $locationProvider.html5Mode({
                enabled: true,
                requireBase: false
            }).hashPrefix("!"); // enable the new HTML5 routing and history API
            // return $locationProvider.html5Mode(true).hashPrefix("!"); // enable the new HTML5 routing and history API
        }
    ]).config([
        '$cryptoProvider', function($cryptoProvider){
            $cryptoProvider.setCryptographyKey('0123456789012345');
        }
    ]);

app.factory('loader', function() {

    var SUCCESS_CODE= 1
    var DATABASE_ERROR_CODE = 2
    var INTERNAL_SERVER_ERROR_CODE = 3
    var SESSION_ERROR_CODE = 4
    var GENERAL_ERROR_CODE = 5

    function checkIfThereIsError($scope, responseData) {
        console.log(responseData);
        var status = responseData.status_code;

        $scope.showStatusMessage = true;
        setTimeout(function() {
            $scope.showStatusMessage = false;
        }, 3000);

        if(status == SESSION_ERROR_CODE) {
            console.log("Session Error");
            $scope.go("/login");
            return false;
        } else if(status != SUCCESS_CODE) {
            console.log("Other error: " + responseData.status);
            $scope.statusMessage = responseData.status;
            return false;
        }
        return true;
    };

    return {
        executeGet: function($scope, request, f) {
            $scope.progressbar.start();
            request.get(function(response) {
                $scope.progressbar.complete();
                if(checkIfThereIsError($scope, response)) {
                    f(response)
                }

            });
        },
        executePOST: function($scope, request, data, f) {
            $scope.progressbar.start();
            request.save(data, function(response) {
                $scope.progressbar.complete();
                if(checkIfThereIsError($scope, response)) {
                    f(response)
                }
            });
        }
    };
});

app.controller("AppCtrl", ["$scope","$resource", "$location", "apiUrl", "$crypto", "loader", "ngProgressFactory", function($scope, $resource, $location, apiUrl, $crypto, loader, ngProgressFactory) {
    //
    //var encrypted = $crypto.encrypt('some plain text data');
    //console.log("encrypted: " + encrypted);
    //
    //var decrypted = $crypto.decrypt(encrypted);
    //console.log("dencrypted: " + decrypted);
    $scope.showStatusMessage = false;

    $scope.go = function (path) {
        $location.path(path);
    };

    $scope.logout = function () {
        var LogoutRequest = $resource(apiUrl + "user/logout");
        loader.executeGet($scope, LogoutRequest, function(response) {
            window.location = "/login";
        });
    };

    $scope.progressbar = ngProgressFactory.createInstance();
    $scope.progressbar.setHeight('4px');
    $scope.progressbar.setColor('#34B7E3');

}]);


    // the global controller
app.controller("HomeCtrl", ["$scope", "$resource", "$location", "apiUrl", "loader", "$crypto", function($scope, $resource, $location, apiUrl, loader, $crypto) {

    $scope.shouldShowSecretInput = false;



    getTexts = function() {
        var SecureTextList = $resource(apiUrl + "user/secure/text/all"); // a RESTful-capable resource object
        loader.executeGet($scope, SecureTextList, function(response) {
            $scope.texts = [];
            var i = 0;
            response.data.textList.forEach(function(textObj) {
                $scope.texts[i] = textObj.text;
                i = i+1;
            });


            //console.log($scope.texts[0].text);
            //var decrypted = $crypto.decrypt($scope.texts[1].text, "0123456789012345");
            //console.log($crypto.decrypt($scope.texts[2].text, "0123456789012345"));
            //console.log("3: "+ decrypted);

        });
    };
    getTexts();

    $scope.addText = function() {
        console.log($scope.text);
        var AddTextRequest = $resource(apiUrl + "user/secure/text");
        loader.executePOST($scope, AddTextRequest, $scope.text, function (response) {
            getTexts();
        });
    };

    $scope.decryptTexts = function() {
        $scope.shouldShowSecretInput = true;
        console.log($scope.text);
        var SendSecret = $resource(apiUrl + "user/send/secret");
        loader.executeGet($scope, SendSecret, function(response) {

            $scope.latestEncryptedData = response.data.textList;
            $scope.latestRESTSecret = response.secret;
        });
    };

    $scope.useThisSecret = function() {
        console.log("concatenate REST secret: " + $scope.latestRESTSecret + " with SMS secret: " + $scope.secret.value);
        var i = 0;
        $scope.texts = [];
        var secret = $scope.latestRESTSecret + $scope.secret.value;
        $scope.latestEncryptedData.forEach(function(textObj) {
            $scope.texts[i] = $crypto.decrypt(textObj.text, secret);
            i = i+1;
        });
        $scope.shouldShowSecretInput = false;

        setTimeout(function() {
            $scope.texts = null;
            window.location.reload(true);
        }, 30000);
    };
}]);

app.controller("RegisterCtrl", ["$scope", "$resource", "$location", "apiUrl","loader", function($scope, $resource, $location, apiUrl, loader) {
    // to save a celebrity

    //function checkPassword(str) { // at least one number, one lowercase and one uppercase letter // at least six characters
    //    var re = /(?=.*\d)(?=.*[a-z])(?=.*[A-Z]).{8,}/;
    //    return re.test(str);
    //}

    $scope.register = function() {
        var RegisterRequest = $resource(apiUrl + "user/register"); // a RESTful-capable resource object
        console.log($scope.user);

        loader.executePOST($scope, RegisterRequest, $scope.user, function(response) {
            if(response.status_code == 1) {
                window.location = "/login";
            }
        }); // $scope.celebrity comes from the detailForm in public/html/detail.html

    };
}]);

app.controller("LoginCtrl", ["$scope", "$resource", "$location", "apiUrl","loader", function($scope, $resource, $location, apiUrl, loader) {
    // to save a celebrity
    $scope.login = function() {
        console.log($scope.credentials);
        var LoginRequest = $resource(apiUrl + "user/login"); // a RESTful-capable resource object
        loader.executePOST($scope, LoginRequest, $scope.credentials, function(response) {
            if(response.status_code == 1) {
                window.location = "/";
            }
        });
    };
}]);