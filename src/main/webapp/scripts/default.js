$(function() {

    $('input:text').addClass("ui-corner-all");

    $('.openModal').click(function() {
        $('#modalContent').load($(this).attr('id'), function() {
            $('#mainModal').modal();

            $(".actionModal").click(function() {
                var $this = $(this);
                var url = $(this).attr("href");

                $.post(url, "", function(resultado) {
                    if (resultado["type"]) {
                        var type = unescape(resultado["type"]);
                        if (type === "success") {
                            updateTableUsers($this);
                            $this.siblings(".success").removeClass("hidden");
                            $this.remove();
                        }
                        else {
                            //mensagem de erro.
                            $("#errorThrown").text(unescape(resultado["message"]));
                            $("#modalMsg").modal();
                        }
                    } else {
                        $("#errorThrown").text("Erro! Não foi possível executar a operação!");
                        $("#modalMsg").modal();
                    }
                }, "json").error(function() {
                    $("#errorThrown").text("Erro! Não foi possível executar a operação!");
                    $("#modalMsg").modal();
                });

            });
        });

    });

    var updateTableUsers = function(btAction) {
        var thisTd = btAction.parent();
        var tempName = thisTd.siblings(".tdName").text();
        var tempLogin = thisTd.siblings(".tdLogin").text();
        var tempRole = thisTd.siblings(".tdRole").text();
        var userId = btAction.attr("userid");
        console.log("id: " + userId + " nome: " + tempName + " login: " + tempLogin + " role:" + tempRole)
        $("#table-users tbody").append($("#table-users tr").last().clone());
        var newTr = $("#table-users tr").last();
        newTr.find(".tdName").text(tempName);
        newTr.find(".tdLogin").text(tempLogin);
        newTr.find(".tdRole").text(tempRole);

        var hrefEditar = newTr.find(".editar").attr("href");
        var newHrefEditar = hrefEditar.replace(/[\d]+/, userId);
        newTr.find(".editar").attr("href", newHrefEditar);

        var hrefExcluir = newTr.find(".delete").attr("href");
        var newHrefExcluir = hrefExcluir.replace(/[\d]+/, userId);
        console.log(newHrefExcluir);
        newTr.find(".delete").attr("href", newHrefExcluir);
    };



    /*Dialogo de confirmacao*/
    $('.confirmLink').on("click",function(e) {
        e.preventDefault();
        $('#actionModalConfirm')
                .attr('href', $(this).attr('href'))
                .attr('idObj', $(this).attr('id'));

        $("#msgApagar").text($(this).attr('title'));
        $("#confirmModal").modal();
    });

    $('#actionModalConfirm').on("click",function(e) {
        e.preventDefault();
        _thisButton = $(this);

        $.post($(this).attr('href'), "", function(resultado) {
            if (resultado["type"]) {
                var type = unescape(resultado["type"]);
                if (type === "success") {
                    var container = $('#container' + _thisButton.attr('idObj'));
                    $('#confirmModal').modal('hide');
                    container.fadeOut(1000, function() {
                        container.remove();
                    });
                } else {
                    $('#confirmModal').modal('hide');
                    $("#textStatus").text(type);
                    $("#errorThrown").text(unescape(resultado["message"]));
                    if (type === 'warn') {
                        $("#error-type").text("Warn: ");
                    }
                    $("#modalMsg").modal();
                }
            } else {
                $('#confirmModal').modal('hide');
                $("#errorThrown").text("Erro! Não foi possível executar a operação!");
                $("#modalMsg").modal();
            }
        }, "json")
                .error(function() {
                    $('#confirmModal').modal('hide');
                    $("#errorThrown").text("Erro! Não foi possível executar a operação!");
                    $("#modalMsg").modal();
                });

        //todo: colocar aqui um gif loading
    });

});
