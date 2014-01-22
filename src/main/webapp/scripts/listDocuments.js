$(function() {
    
    $('.confirmLink').click(function(e) {
       $('#confirmExclude').attr('href', $(this).attr('href'));
       $(this).parent().parent().parent().attr('id','doc_deletar');
    });
    
    $('#confirmExclude').click(function(e){
       $.post($(this).attr('href'), "", function(resultado) {
            if (resultado["type"]) {
                var type = unescape(resultado["type"]);
                if (type == "error") {
                    $('#excluir_documento').modal('hide');
                    $("#textStatus").text(type);
                    $("#errorThrown").text(unescape(resultado["message"]));
                    if (type == 'warn') {
                        $("#error-type").text("Warn: ");
                    }
                    $('#nao_excluido').modal();
                } else {
                    $('#excluir_documento').modal('hide');
                    $('#doc_deletar').fadeOut(1000, function() { $('#doc_deletar').remove(); });
                    $('#excluido_com_sucesso').modal();
                }
            } else {
                $('#excluir_documento').modal('hide');
                $("#errorThrown").text("Não foi possível executar a operação!");                
            }
        }, "json")
                .error(function(jqxhr) {
            $('#excluir_documento').modal('hide');
            if (jqxhr.status == 403) {
                $("#errorThrown").text("Você não possui permissão para deletar esse arquivo!");
            } else {
                $("#errorThrown").text("Não foi possível executar a operação!");
            }

        });
      
      return false;
       
   });
                               
    $('.relation_link')
    .button({
        icons: {
            primary: "ui-icon-triangle-1-s"
        }
    })
    .click(function(){
        var caixa = $(this).siblings('.relation_dropdown');
        caixa.toggle()
        .addClass("dropDownBox");
        
        var btnLeft= $(this).offset().left;
        var btnTop = $(this).offset().top + $('.relation_link').outerHeight();
        var btnWidth = $(this).outerWidth();                    
        
        caixa.css({                        
            'left': btnLeft,
            'top': btnTop,                        
            'width': btnWidth                        
        })
    });
});