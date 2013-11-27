$(function() {
       
    $('input:text').addClass("ui-corner-all");
    
    $(".btSemTexto").button({
        text: false
    });
    
    /*Dialogo de confirmacao*/
    /*$( ".dialog-confirm" ).dialog({
        resizable: true,
        width:440,
        autoOpen: false,
        modal: true,
        buttons: [
        {
            text: "Apagar",
            id: "botaoDialogo",
            click: function() {
                _thisDialog = $(this);
                $.post($(this).data('url'),"",function(resultado){
                    if(resultado["type"]){
                        var type = unescape(resultado["type"]);
                        if(type =="error" || type == "warn"){
                            _thisDialog.dialog('close');
                            $("#textStatus").text(unescape(resultado["type"]));
                            $("#errorThrown").text(unescape(resultado["message"]));
                            if(type == 'warn'){
                                $("#error-type").text("Warn: ");
                            }
                            $("#dialog-error").dialog('open');
                        }else{
                            _thisDialog.dialog("close");
                            //$(window.document.location).attr('href',window.grecoUrlRoot +unescape(resultado["href"]));
                            location.reload();
                        }
                    }else{
                        _thisDialog.dialog('close');
                        $("#errorThrown").text("Não foi possível executar a operação!");
                        $("#dialog-error").dialog('open');
                    }
                },"json")
                .error(function() {
                    _thisDialog.dialog('close');
                    $("#errorThrown").text("Não foi possível executar a operação!");
                    $("#dialog-error").dialog('open');
                });

                $( ".dialog-confirm" ).siblings(".ui-dialog-buttonpane").hide();
                $( ".dialog-confirm" ).html("<p> Excluindo... Por favor aguarde.</p>");
            }
        },
        {
            text: "Cancelar",
            click: function() {
                $( this ).dialog( "close" );
            }
        }
        ]
    });
    
    $( "#dialog-error" ).dialog({
        modal: true,
        autoOpen: false,
        buttons: {
            Ok: function() {
                $( this ).dialog( "close" );
                location.reload();
            }
        }
    });*/
        
    $('.confirmLink').click(function(e) {
        e.preventDefault();
        $(".dialog-confirm")
        .data('url', $(this).attr('href')) // sends url to the dialog
        .dialog('open'); // opens the dialog
        $("#msgApagar").text($(this).attr('title'));
    //        $(" #botaoDialogo ").children("span").text($(this).text()); //altera o texto do botão submit do dialog
    });
    
    
});
