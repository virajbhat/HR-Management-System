app.component('timesheetComponent', {
  bindings: {},
  templateUrl: './src/components/timesheet-component/timesheet.component.html',
  controller: timesheetController
});

function timesheetController(
  $scope,
  $mdDialog,
  $stateParams,
  timesheetService
) {
  var $ctrl = this;
  $ctrl.dayNames = ['Sun', 'Mon', 'Tue', 'Wed', 'Thu', 'Fri', 'Sat'];
  $scope.day = moment();
  $ctrl.taskList = [];
  $scope.startDate = null;
  $scope.endDate = null;

  $ctrl.onInit = function() {
    // getTimesheetDetails goes here..
  };

  $ctrl.series = ['Hours Worked'];

  $ctrl.data = [10];

  $ctrl.addTask = function(event) {
    $mdDialog
      .show({
        controller: DialogController,
        templateUrl: './src/dialogs/timesheet.addtask.html',
        parent: angular.element(document.body),
        targetEvent: event,
        locals: {
          date: $scope.day
        },
        clickOutsideToClose: true,
        fullscreen: false // Only for -xs, -sm breakpoints.
      })
      .then(
        function(formData) {
          timesheetService
            .addTask(
              $stateParams.empId,
              formData.swipeIn,
              formData.swipeOut,
              $scope.day._d.getTime(),
              formData.taskName
            )
            .then(res => console.log(res));
          (formData = { ...formData, taskDate: $scope.day._d.getTime() }),
            $ctrl.taskList.push(formData);
        },
        function() {} //fires when dialog closed
      );
  };
}

function DialogController($scope, $mdDialog, date) {
  $scope.date = date;
  $scope.hide = function() {
    $mdDialog.hide();
  };

  $scope.cancel = function() {
    $mdDialog.cancel();
  };

  $scope.answer = function(event, answer) {
    event.preventDefault();
    $mdDialog.hide(answer);
  };
}
