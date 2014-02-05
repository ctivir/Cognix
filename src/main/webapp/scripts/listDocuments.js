$(function() {
    
//    $('.confirmLink').click(function(e) {
//       $('#confirmExclude').attr('href', $(this).attr('href'));
//       $(this).parent().parent().parent().attr('id','doc_deletar');
//    });
    
    $('#confirmExclude').click(function(e){
       $.post($(this).attr('href'), "", function(resultado) {
            if (resultado["type"]) {
                var type = unescape(resultado["type"]);
                if (type === "error") {
                    $('#confirmModal').modal('hide');
                    $("#textStatus").text(type);
                    $("#errorThrown").text(unescape(resultado["message"]));
                    if (type === 'warn') {
                        $("#error-type").text("Warn: ");
                    }
                    $('#nao_excluido').modal();
                } else {
                    $('#confirmModal').modal('hide');
                    $('#doc_deletar').fadeOut(1000, function() { $('#doc_deletar').remove(); });
                    $('#excluido_com_sucesso').modal();
                }
            } else {
                $('#confirmModal').modal('hide');
                $("#errorThrown").text("Não foi possível executar a operação!");                
            }
        }, "json")
                .error(function(jqxhr) {
            $('#confirmModal').modal('hide');
            if (jqxhr.status === 403) {
                $("#errorThrown").text("Você não possui permissão para deletar esse arquivo!");
            } else {
                $("#errorThrown").text("Não foi possível executar a operação!");
            }

        });
      
      return false;
       
   });

});