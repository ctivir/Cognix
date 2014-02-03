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
                        else{
                            //mensagem de erro.
                            $("#txtErrorModal").text(unescape(resultado["message"]));
                            $("#modal-error").modal();
                        }
                    } else {
                        $("#txtErrorModal").text("Erro! Não foi possível executar a operação!");
                        $("#modal-error").modal();
                    }
                }, "json").error(function() {
                    $("#txtErrorModal").text("Erro! Não foi possível executar a operação!");
                    $("#modal-error").modal();
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
        console.log("id: "+userId+" nome: "+tempName+" login: "+tempLogin+" role:"+tempRole)
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
    /*$( ".dialog-confirm" ).dialog({
     resizable: true,
     width: 440,
     autoOpen: false,
     modal: true,
     buttons: [
     {
     text: "Apagar",
     id: "botaoDialogo",
     click: function() {
     _thisDialog = $(this);
     $.post($(this).data('url'), "", function(resultado) {
     if (resultado["type"]) {
     var type = unescape(resultado["type"]);
     if (type == "error" || type == "warn") {
     _thisDialog.dialog('close');
     $("#textStatus").text(unescape(resultado["type"]));
     $("#errorThrown").text(unescape(resultado["message"]));
     if (type == 'warn') {
     $("#error-type").text("Warn: ");
     }
     $("#dialog-error").dialog('open');
     } else {
     _thisDialog.dialog("close");
     //$(window.document.location).attr('href',window.grecoUrlRoot +unescape(resultado["href"]));
     location.reload();
     }
     } else {
     _thisDialog.dialog('close');
     $("#errorThrown").text("Não foi possível executar a operação!");
     $("#dialog-error").dialog('open');
     }
     }, "json")
     .error(function() {
     _thisDialog.dialog('close');
     $("#errorThrown").text("Não foi possível executar a operação!");
     $("#dialog-error").dialog('open');
     });
     
     $(".dialog-confirm").siblings(".ui-dialog-buttonpane").hide();
     $(".dialog-confirm").html("<p> Excluindo... Por favor aguarde.</p>");
     }
     },
     {
     text: "Cancelar",
     click: function() {
     $(this).dialog("close");
     }
     }
     ]
     });
     
     $("#dialog-error").dialog({
     modal: true,
     autoOpen: false,
     buttons: {
     Ok: function() {
     $(this).dialog("close");
     location.reload();
     }
     }
     });*/

    $('.confirmLink').click(function(e) {
        e.preventDefault();
        /*$(".dialog-confirm")
         .data('url', $(this).attr('href')) // sends url to the dialog
         .dialog('open'); // opens the dialog
         $("#msgApagar").text($(this).attr('title')); */
        //        $(" #botaoDialogo ").children("span").text($(this).text()); //altera o texto do botão submit do dialog
    });

});
