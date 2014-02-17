$(function() {

    $(document).on("click", ".openModalForm", function() {
        $('#modalContentForm').load($(this).attr('href'), function() {
            // abrir a modal somente apos carregar o conteudo
            $('#modalForm').modal();
        });
    });

    $(document).on("click", "#submitForm", function() {
        var url = $("#entityUpdateForm").attr("action");

        $.post(url, $("#entityUpdateForm").serialize()).done(function(data, status, jqxhr) {
            if (jqxhr.status === 200) {
                $("#modalContentForm").empty().append(data);
            }
            else {
                //testa se esta editando ou salvando novo usuario
                if (url.match("/edit$")) {
                    var trNew = $(data).find("tbody tr");
                    var trOld = $("#"+trNew.attr("id"));
                    trOld.html(trNew.html());
                } else {
                    $("#table-users tbody").append($(data).find("tbody tr"));
                }
                $('#modalForm').modal('hide');
            }
        });
    });

    $(document).on("click", ".enableUser", function() {
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

    var updateTableUsers = function(btAction) {
        var thisTd = btAction.parent();
        var tempName = thisTd.siblings(".tdName").text();
        var tempLogin = thisTd.siblings(".tdLogin").text();
        var tempRole = thisTd.siblings(".tdRole").text();
        var userId = btAction.attr("userid");
        $("#table-users tbody").append($("#table-users tr").last().clone());
        var newTr = $("#table-users tr").last();
        newTr.attr("id", "container" + userId);
        newTr.find(".tdName").text(tempName);
        newTr.find(".tdLogin").text(tempLogin);
        newTr.find(".tdRole").text(tempRole);

        var hrefEditar = newTr.find(".editar").attr("href");
        var newHrefEditar = hrefEditar.replace(/[\d]+/, userId);
        newTr.find(".editar").attr("href", newHrefEditar);

        var hrefExcluir = newTr.find(".delete").attr("href");
        var newHrefExcluir = hrefExcluir.replace(/[\d]+/, userId);
        newTr.find(".delete")
                .attr("href", newHrefExcluir)
                .attr("id", userId)
                .attr("title", "excluir o usuário " + tempName + "?");
    };

});
