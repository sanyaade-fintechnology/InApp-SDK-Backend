
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

app.controller('paymentController', function ($scope, Restangular, $uibModal) {

    function getData(itemsPerPage) {

        var payments = Restangular.all('payments');

        var queryParameters = {
                                page: $scope.currentPage - 1,
                                pageSize: itemsPerPage
                              };

        payments.getList(queryParameters).then(function(payments)  {
            $scope.payments = payments;
            $scope.totalItems = payments.meta.totalElements;
        }).catch(function(response) {
            if(response.status === 403 || response.status === 401) {
               $scope.totalItems = 0;
               $scope.payments = [];
               $scope.currentPage = 1;
               return false; // error handled
            }
            return true; // error not handled
        });
    }

    $scope.currentPage = 1;
    $scope.payments = [];
    $scope.itemsPerPage = 20;


    $scope.pageChanged = function() {
        getData($scope.itemsPerPage);

    };

    getData($scope.itemsPerPage);

    $scope.setSuccess = function(paymentId) {
        Restangular.one('payments', paymentId).get().then(function(loadedPayment) {
            loadedPayment.status = 'OK';
            loadedPayment.put().then(function() {
                getData($scope.itemsPerPage);
            });
        });
    }

    $scope.setFailed = function(paymentId) {
        Restangular.one('payments', paymentId).get().then(function(loadedPayment) {
                loadedPayment.status = 'NOK';
                loadedPayment.put().then(function() {
                    getData($scope.itemsPerPage);
                });
            });
    }

    $scope.alerts = [];

    function addAlert(alert) {
        $scope.alerts.push(alert);
    }

    $scope.closeAlert = function(index) {
        $scope.alerts.splice(index, 1);
    };

    $scope.openCreateChargeModal = function() {

      var modalInstance = $uibModal.open({
                                animation: true,
                                templateUrl: 'createChargeModal',
                                controller: 'chargeModalController',
                                size: 'lg'});

      modalInstance.result.then(function (newCharge) {

        var charges = Restangular.all('charges');

        charges.post(newCharge).then(function(payment) {
            if (payment.status == 'NOK') {
                addAlert({type:'warning', msg: 'Charge failed. Please check logs for reason.'});
            }
            getData($scope.pageSize);
        }, function() {
            addAlert({type:'danger', msg: 'Save failed.'});
        });
      });
    };

    $scope.openCreateRefundModal = function(orderNumber, merchantToken) {

          var modalInstance = $uibModal.open({
                                    animation: true,
                                    templateUrl: 'createRefundModal',
                                    controller: 'refundModalController',
                                    size: 'lg',
                                    resolve: {
                                      orderNumber: function() {
                                        return orderNumber;
                                      },
                                      merchantToken: function() {
                                        return merchantToken;
                                      }
                                    }
                                  });

          modalInstance.result.then(function (newRefund) {

            var refunds = Restangular.all('refunds');

            refunds.post(newRefund).then(function(payment) {
                if (payment.status == 'NOK') {
                    addAlert({type:'warning', msg: 'Refund failed. Please check logs for reason.'});
                }
                getData($scope.pageSize);
            }, function() {
                addAlert({type:'danger', msg: 'Save failed.'});
            });
          });
        };

});

app.controller('chargeModalController', function ($scope, $modalInstance) {
  $scope.createCharge = function () {

  var newCharge = {'merchantToken': $scope.merchantToken,
                           'userToken': $scope.userToken,
                           'amount': $scope.amount,
                           'currencyCode': $scope.currencyCode,
                           'orderNumber': $scope.orderNumber};
    $modalInstance.close(newCharge);
  };
  $scope.cancelCharge = function () {
    $modalInstance.dismiss('cancel');
  };
});

app.controller('refundModalController', function ($scope, $modalInstance, merchantToken, orderNumber) {

  $scope.merchantToken = merchantToken;
  $scope.orderNumber = orderNumber;

  $scope.createRefund = function () {

    var newRefund = {'merchantToken': $scope.merchantToken,
                             'orderNumber': $scope.orderNumber,
                             'amount': $scope.amount,
                             'currencyCode': $scope.currencyCode};
      $modalInstance.close(newRefund);
    };
    $scope.cancelRefund = function () {
      $modalInstance.dismiss('cancel');
    };
});