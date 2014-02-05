$(function() {

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
        newTr.attr("id", "container"+userId);
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
                .attr("title", "excluir o usuário "+tempName+"?");
    };

    /*
     
     $(".editar").click(function(ev) {
     ev.preventDefault();
     var link = $(this).attr('href');
     openDialog(link);
     }); 
     
     $( "#dialog-form" ).dialog({
     autoOpen: false,
     modal: true,
     width: 655,
     //        height: $(window).height()-10,
     buttons: {
     "Salvar": function() {
     var d = $(this);
     $.post($("#entityUpdateForm").attr("action"), $("#entityUpdateForm").serialize()).done( function(data, status, jqxhr) {
     if(jqxhr.status == 200) {                        
     $( "#dialog-form" ).empty().append( data );
     
     }else {                        
     $( "#dialog-form" ).empty();
     $( d ).dialog( "close" );
     window.location.reload();
     }
     });
     
     },
     "Cancelar": function() {
     $( this ).empty();
     $( this ).dialog( "close" );
     }
     },
     close: function() {
     $( this ).empty();
     }
     });*/
});
