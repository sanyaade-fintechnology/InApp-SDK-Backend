
var app = angular.module('sampleApp',['restangular','ui.bootstrap']);

app.config(function(RestangularProvider) {

     Date.prototype.addHours= function(h){
                this.setHours(this.getHours()+h);
                return this;
            };

    RestangularProvider.addResponseInterceptor(function(data, operation, what, url, response, deferred) {
              var extractedData;
              // .. to look for getList operations
              if (operation === "getList") {
                // .. and handle the data and meta data
                extractedData = data.content;
                extractedData.meta = {'totalElements':data.totalElements};
              } else {
                extractedData = data;
              }
              return extractedData;
            });

});

app.controller('merchantController', function ($scope, Restangular) {

    function getData(itemsPerPage) {

        var payments = Restangular.all('merchants');

        var queryParameters = {
                                page: $scope.currentPage - 1,
                                pageSize: itemsPerPage
                              };

        payments.getList(queryParameters).then(function(merchants)  {
            $scope.merchants = merchants;
            $scope.totalItems = merchants.meta.totalElements;
        }).catch(function(response) {
            if(response.status === 403 || response.status === 401) {
               $scope.totalItems = 0;
               $scope.merchants = [];
               $scope.currentPage = 1;
               return false; // error handled
            }
            return true; // error not handled
        });
    }

    $scope.currentPage = 1;
    $scope.merchants = [];
    $scope.itemsPerPage = 20;

    $scope.pageChanged = function() {
        getData($scope.itemsPerPage);

    };

    getData($scope.itemsPerPage);

    $scope.alerts = [];

    function addAlert(alert) {
        $scope.alerts.push(alert);
    }

    $scope.closeAlert = function(index) {
        $scope.alerts.splice(index, 1);
    };

    $scope.create = function(countryCode, type) {

        var newMerchant = {'countryCode' : countryCode, 'type': type};

        var merchants = Restangular.all('merchants');

        merchants.post(newMerchant).then(function(merchant) {
            if (merchant.merchantToken == null) {
                addAlert({type:'warning', msg: 'Create failed. Please check logs for reason.'});
            }
            getData($scope.pageSize);
        }, function() {
            addAlert({type:'danger', msg: 'Save failed.'});
        });

    };
});