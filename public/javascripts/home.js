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
    return {
        executeGet: function(request, $scope, f) {
            $scope.progressbar.start();
            request.get(function(response) {
                $scope.progressbar.complete();
                f(response)
            });
        },
        executePOST: function(request, data, $scope, f) {
            $scope.progressbar.start();
            request.save(data, function(response) {
                $scope.progressbar.complete();
                f(response)
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

    $scope.go = function (path) {
        $location.path(path);
    };

    $scope.progressbar = ngProgressFactory.createInstance();
    $scope.progressbar.setHeight('4px');
    $scope.progressbar.setColor('#34B7E3');

    //
    //var PatientList = $resource(apiUrl + "/home/all"); // a RESTful-capable resource object
    //PatientList.get(function(response) {
    //    $scope.patients = response.data.patientList;
    //
    //});
    //
    //var BeaconList = $resource(apiUrl + "/beacon/all"); // a RESTful-capable resource object
    //BeaconList.get(function(response) {
    //    console.log(response);
    //    $scope.beacons = response.data.beaconList;
    //
    //});
    //
    //var BedList = $resource(apiUrl + "/bed/all"); // a RESTful-capable resource object
    //BedList.get(function(response) {
    //    console.log(response);
    //    $scope.beds = response.data.bedList;
    //
    //});
    //
    //var EquipmentList = $resource(apiUrl + "/equipment/all"); // a RESTful-capable resource object
    //EquipmentList.get(function(response) {
    //    console.log(response);
    //    $scope.equipments = response.data.equipmentList;
    //
    //});
    //
    //
    //$scope.onClickDetail = function(detailUrl) {
    //    window.location = detailUrl;
    //};
    //
    //$scope.isActive = function(viewLocation) {
    //
    //    var active = false;
    //
    //    if(viewLocation === $location.path()){
    //        active = true;
    //    }
    //
    //    return active;
    //};
}]);


    // the global controller
app.controller("HomeCtrl", ["$scope", "$resource", "$location", "apiUrl", "loader", "$crypto", function($scope, $resource, $location, apiUrl, loader, $crypto) {


    console.log("in HomeController");

    $scope.shouldShowSecretInput = false;



    getTexts = function() {
        var SecureTextList = $resource(apiUrl + "user/secure/text/all"); // a RESTful-capable resource object
        loader.executeGet(SecureTextList, $scope, function(response) {
            $scope.texts = response.data.textList;

            console.log($scope.texts[0].text);
            var decrypted = $crypto.decrypt($scope.texts[0].text);
            console.log("3: "+ decrypted);

        });
    };
    getTexts();

    $scope.addText = function() {
        console.log($scope.text);
        var AddTextRequest = $resource(apiUrl + "user/secure/text");
        loader.executePOST(AddTextRequest, $scope.text, $scope, function (response) {
            console.log(response);
            getTexts();
        });
    };

    $scope.decryptTexts = function() {
        $scope.shouldShowSecretInput = true;
        console.log($scope.text);
        var SendSecret = $resource(apiUrl + "user/send/secret")
        SendSecret.get(function(response) {
            $scope.statusMessage = "secret is sended"

        });
    }

    $scope.useThisSecret = function() {
        $scope.statusMessage = ""
    };
}]);

app.controller("RegisterCtrl", ["$scope", "$resource", "$location", "apiUrl", function($scope, $resource, $location, apiUrl) {
    // to save a celebrity

    //function checkPassword(str) { // at least one number, one lowercase and one uppercase letter // at least six characters
    //    var re = /(?=.*\d)(?=.*[a-z])(?=.*[A-Z]).{8,}/;
    //    return re.test(str);
    //}

    $scope.register = function() {
        var RegisterRequest = $resource(apiUrl + "user/register"); // a RESTful-capable resource object
        console.log($scope.user);
        RegisterRequest.save($scope.user, function(response) {
            if(response.result.status_code == 1) {
                window.location = "/login";
            }
        }); // $scope.celebrity comes from the detailForm in public/html/detail.html

    };
}]);

app.controller("LoginCtrl", ["$scope", "$resource", "$location", "apiUrl", function($scope, $resource, $location, apiUrl) {
    // to save a celebrity
    $scope.login = function() {
        console.log($scope.credentials);
        var loginCtrl = $resource(apiUrl + "user/login"); // a RESTful-capable resource object
        loginCtrl.save($scope.credentials, function(response) {
            console.log(response);
            if(response.result.status_code == 1) {
                window.location = "/";
            }
        });
    };
}]);


app.controller("patientController", ["$scope","$resource", "$location", "apiUrl", function($scope, $resource, $location, apiUrl) {

       $scope.addPatient = function() {
            console.log($scope.patient)
            var CreatePatientFromRequested = $resource(apiUrl + "/home/add")
            CreatePatientFromRequested.save($scope.patient, function(response) {
                //if($rootScope.checkError(response))
        });
    }
}]);

app.controller("beaconPatientLinkerController", ["$scope","$resource", "$location", "apiUrl", function($scope, $resource, $location, apiUrl) {


   $scope.linkBeaconWithPatient = function() {
        var CreateLinkFromRequested = $resource(apiUrl + "/beacon/set/bed")
        CreateLinkFromRequested.save($scope.linker, function(response) {
            //if($rootScope.checkError(response))
         });
    }
}]);
