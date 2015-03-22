var module = angular.module("board", ['ngRoute', 'ngResource', 'spring-security-csrf-token-interceptor']);
module.config(["$routeProvider",
    function ($routeProvider) {
        $routeProvider.
                when("/board/:uuid", {
                    templateUrl: 'tpl/board.html',
                    controller: 'BoardRouteController',
                    resolve: {
                        currentBoard: function ($route, Board) {
                            return Board.get({uuid: $route.current.params.uuid}).$promise;
                        }
                    }
                }).
                when("/", {
                    templateUrl: 'tpl/create_board.html',
                    controller: 'CreateBoardController'
                }).
                otherwise({
                    redirectTo: '/'
                });
    }
]);

module.controller("CreateBoardController", ['$scope', '$http', '$location', function ($scope, $http, $location) {
    $scope.createBoardForm = {};
    $scope.createBoardForm.submitTheForm = function (item, event) {
        $http.post("boards", {})
                .success(function (dataFromServer, status, headers, config) {
                    $location.path("/board/" + dataFromServer.uuid);
                })
                .error(function (data, status, headers, config) {
                    alert("Submitting form failed! Please try again later");
                });
    };
}]);

module.directive('post', ['Position', 'BoardLiveService', function (Position, BoardLiveService) {
    return {
        restrict: 'C',
        link: function ($scope, element, attr) {
            if (!element.scope().post.position.isUnknown()) {
                $(element).offset(element.scope().post.position.asOffset());
                $(element).zIndex(element.scope().post.position.z);
            }

            var positionHandler = function (event, ui) {
                // Expand board when post is dragged out of its current bounds
                var topDiff = ui.helper.offset().top + ui.helper.height() - $("#board").height();
                if (topDiff > 0) {
                    $("#board").height($("#board").height() + topDiff + 10);
                }
                var leftDiff = ui.helper.offset().left + ui.helper.width() - $("#board").width();
                if (leftDiff > 0) {
                    $("#board").width($("#board").width() + leftDiff + 10);
                }

                $scope.$apply(function () {
                    ui.helper.scope().post.position = Position.buildFromOffset(ui.helper.offset());
                    ui.helper.scope().post.position.z = ui.helper.zIndex();
                });
            };

            var sendUpdateHandler = function (event, ui) {
                BoardLiveService.sendTransientUpdate(ui.helper.scope().post);
            };

            element.draggable({
                stack: '.post',
                handle: '.drag_handle',
                drag: positionHandler,
                stop: positionHandler
            });
            //element.on("drag", sendUpdateHandler);
        }
    };
}]);

module.directive('contenteditable', function () {
    return {
        restrict: "A",
        require: "ngModel",
        link: function (scope, element, attrs, ngModel) {

            function read() {
                ngModel.$setViewValue(element.text());
            }

            ngModel.$render = function () {
                element.text(ngModel.$viewValue || "");
            };

            element.bind("blur keyup change", function () {
                scope.$apply(read);
            });

            element.bind("blur", function () {
                if (element.parent().parent().hasClass('remote')) {
                    element.scope().post.$save({boardId: scope.board.uuid});
                }
            });
        }
    };
});

module.directive('postTarget', function () {
    return {
        restrict: 'C',
        link: function ($scope, element, attr) {
            var isGreedy = element.hasClass('local');
            element.droppable({
                greedy: isGreedy,
                accept: ".post",
                drop: function (event, ui) {
                    var post = ui.draggable.scope().post;
                    if ($(this).hasClass('remote')) {
                        target = $scope.remotePosts;
                        source = $scope.localPosts;
                    } else {
                        target = $scope.localPosts;
                        source = $scope.remotePosts;
                    }

                    var zmax = 0;
                    $.each(target, function (index, value) {
                        var cur = value.position.z;
                        zmax = cur > zmax ? value.position.z : zmax;
                    });
                    $scope.$apply(function () {
                        post.position.z = Number(zmax) + 1;
                    });
                    if ($.inArray(post, target) > -1) {
                        if ($(this).hasClass('remote')) {
                            post.$save({boardId: $scope.board.uuid});
                        }
                        return;
                    }

                    if ($(this).hasClass('remote')) {
                        post.$save({boardId: $scope.board.uuid});
                    } else {
                        post.$remove({boardId: $scope.board.uuid});
                        delete post.id;
                    }


                    $scope.$apply(function () {
                        if ($.inArray(post, source) > -1) {
                            source.splice($.inArray(post, source), 1);
                        }
                        target.push(post);
                    });
                }
            });
        }
    };
});

module.factory('Position', function () {

    function Position(top, left, zIndex) {
        this.y = top;
        this.x = left;
        this.z = zIndex;
    }

    Position.prototype.isUnknown = function () {
        return (this.x === null) || (this.y === null) || (this.z === null);
    };
    Position.prototype.asOffset = function () {
        return {
            top: this.y,
            left: this.x
        };
    };
    Position.build = function (data) {
        return new Position(
                data.y,
                data.x,
                data.z
                );
    };
    Position.buildFromOffset = function (offset) {
        return new Position(
                offset.top,
                offset.left,
                0
                );
    };
    return Position;
});

module.factory('Board', ['$resource', 'Post', function ($resource, Post) {
        var Board = $resource(
                'boards/:uuid',
                {},
                {
                    get: {
                        method: 'GET',
                        isArray: false,
                        transformResponse: function (data, header) {
                            var board = angular.fromJson(data);
                            var enhancedPosts = [];
                            if (undefined !== board.posts) {
                                for (var i = 0; i < board.posts.length; i++) {
                                    enhancedPosts.push(Post.build(board.posts[i]));
                                }
                            }
                            board.posts = enhancedPosts;
                            return board;
                        }
                    }
                });
        return Board;
    }]);

module.factory('Post', ['$resource', 'Position', function ($resource, Position) {

        var Post = $resource(
                'boards/:boardId/posts/:postId',
                {
                    boardId: '@boardId',
                    postId: '@id'
                },
        {
            get: {
                method: 'GET',
                isArray: false,
                transformResponse: function (data, header) {
                    var post = angular.fromJson(data);
                    post.position = Position.build(post.position);
                    return post;
                }
            },
            save: {
                method: 'POST',
                transformResponse: function (data, header) {
                    var post = angular.fromJson(data);
                    post.position = Position.build(post.position);
                    return post;
                }
            }
        }
        );

        angular.extend(Post.prototype, {
            position: new Position(),
            content: ''
        });
        Post.build = function (data) {
            var newPost = new Post(data);
            newPost.position = Position.build(data.position);
            return newPost;
        };
        return Post;
    }]);

module.service("BoardLiveService", ['$q', '$timeout', 'Post', function ($q, $timeout, Post) {

    var service = {};
    var listener = $q.defer();
    var socket = {
        client: null,
        stomp: null
    };

    var boardId;

    service.RECONNECT_TIMEOUT = 1000;
    service.SOCKET_URL = window.location.protocol + '//' + window.location.hostname + ":8080/board/socket";
    service.BASE_TOPIC = "/topic/";
    service.PREFIX = "/app/";

    service.receive = function () {
        return listener.promise;
    };

    var reconnect = function () {
        $timeout(function () {
            console.log("trying to reconnect");
            initialize();
        }, this.RECONNECT_TIMEOUT);
    };

    var startListener = function () {
        socket.stomp.subscribe(service.BASE_TOPIC + 'boards/' + boardId, function (data) {
            var envelope = JSON.parse(data.body);
            envelope.payload = Post.build(envelope.payload);
            listener.notify(envelope);
        });
    };

    var initialize = function () {
        socket.client = new SockJS(service.SOCKET_URL);
        socket.stomp = Stomp.over(socket.client);
        socket.stomp.connect("max", "muster", startListener);
        socket.client.onclose = reconnect;
    };

    service.setBoardId = function (uuid) {
        boardId = uuid;
        initialize();
    };

    service.sendTransientUpdate = function (post) {
        socket.stomp.send(service.PREFIX + 'boards/' + boardId, {}, JSON.stringify(post));
    };

    return service;
}]);

module.controller("BoardRouteController", ['$scope', 'Post', 'BoardLiveService', 'currentBoard', function ($scope, Post, BoardLiveService, currentBoard) {
    $scope.board = currentBoard;
    $scope.localPosts = [];
    // use board instead?
    $scope.remotePosts = currentBoard.posts;
    $scope.toggleSidebar = function () {
        var sidebar = $("#sidebar");
        if (sidebar.hasClass("collapsed")) {
            sidebar.animate({left: '0px'}, {queue: false, duration: 500});
            sidebar.removeClass("collapsed");
        } else {
            leftTarget = -sidebar.width() + 20;
            sidebar.animate({left: leftTarget}, {queue: false, duration: 500});
            sidebar.addClass("collapsed");
        }
    };
    $scope.createLocalPost = function () {
        var post = new Post();
        post.content = 'tescht';
        $scope.localPosts.push(post);
    };
    $scope.foregroundPost = function (event, post) {
        var target = $(event.target);
        if (!target.hasClass('post')) {
            target = target.parents('.post').first();
        }
        var zmax = 0;
        target.siblings('.post').each(function () {
            var cur = $(this).zIndex();
            zmax = cur > zmax ? $(this).zIndex() : zmax;
        });
        post.position.z = Number(zmax) + 1;
        /*if (target.scope().post.id !== undefined)
         target.scope().post.$save({boardId: $scope.board.uuid});*/
    };

    BoardLiveService.setBoardId($scope.board.uuid);
    BoardLiveService.receive().then(null, null, function (envelope) {
        if (envelope.type === 'Post') {
            var post = envelope.payload;
            for (var i = 0, len = $scope.remotePosts.length; i < len; i++) {
                if ($scope.remotePosts[i].id === post.id) {
                    // On update refresh copy data to local copy
                    if (envelope.action === 'UPDATE') {
                        $scope.remotePosts[i] = post;
                        // On delete get rid of the local copy
                    } else if (envelope.action === 'DELETE') {
                        $scope.remotePosts.splice(i, 1);
                    }
                    // We want to quite here in all cases where post already exists
                    // In case of CREATE this means we prevent duplicates
                    return;
                }
            }
            if (envelope.action === 'CREATE') {
                $scope.remotePosts.push(envelope.payload);
            }
        }
    });
}]);


