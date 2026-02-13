package br.com.fiap.mschatgateway.application.text;

public final class ChatTexts {

    private ChatTexts() {}


    public static final String INVALID_OPTION =
    "❌ Opção inválida. Por favor, selecione uma das opções informando o número correspondente.";

    public static final String WELCOME = """
    Olá! 👋 Bem-vindo(a) ao Meu Remédio Popular!

    Aqui você pode encontrar farmácias credenciadas, consultar medicamentos e obter informações sobre o programa Farmácia Popular.

    Como posso ajudá-lo(a) hoje?
    """;

    public static final String START_FIND_PHARMACIES = """
    Ótimo! Vamos encontrar as farmácias cadastradas no Farmácia Popular mais próximas.

    Primeiro, selecione o Estado:
    """;

    public static final String START_FIND_MEDICATION = """
    Ótimo! Para buscar medicamentos, primeiro preciso saber a sua localização.

    Selecione o Estado:
    """;

    public static final String FOUND_PHARMACIES_FOR_MEDICATION = """
    Encontramos farmácias credenciadas na sua região.

    Agora vamos escolher o medicamento:
    """;

    public static final String SELECT_CITY = """
    Agora selecione a Cidade:
    """;

    public static final String SELECT_NEIGHBORHOOD = """
    Por último, selecione o Bairro:
    """;

    public static final String FOUND_PHARMACIES = """
    Farmácias credenciadas encontradas:
    
    """;

    public static final String NO_PHARMACIES_FOUND = """
    ❌ Não há farmácias credenciadas na região informada no momento.

    O que deseja fazer?
    """;

    public static final String ASK_SEARCH_MEDICATION = """
    Deseja buscar um medicamento específico?
    """;

    public static final String SELECT_MEDICATION_TYPE = """
    Selecione o tipo de medicamento:
    """;

    public static final String SELECT_MEDICATION = """
    Agora selecione o medicamento:
    """;

    public static final String ASK_FEEDBACK = """
    Consegui te ajudar hoje?
    """;

    public static final String MEDICATION_RESULT_HEADER = """
     Resultado da busca por %s:
     
     """;

    public static final String STOCK_NOT_CONFIRMED = """
    ⚠️ Disponibilidade do medicamento não confirmada no sistema
    Recomendamos verificar diretamente na farmácia.
    """;

    public static final String ASK_VIEW_OTHER_PHARMACIES = """
    Existem outras farmácias credenciadas na região,
    porém não temos confirmação de disponibilidade para este medicamento
    É recomendado verificar diretamente com a farmácia.

    Deseja visualizá-las?
    """;

    public static final String NO_CONFIRMED_STOCK = """
    Não encontramos disponibilidade registrada para %s
    nas farmácias desta região no momento.

    Recomendamos consultar diretamente a farmácia listada na sua região para mais informações.
    """;

    public static final String ABOUT_PROGRAM = """
    📋 Sobre o Farmácia Popular

    O Farmácia Popular é um programa do Governo Federal que amplia o acesso a medicamentos essenciais,
    oferecendo itens gratuitos ou com desconto em farmácias credenciadas.

    O programa atende, principalmente, medicamentos para doenças crônicas como hipertensão,
    diabetes, asma e anticoncepção.

    Mais informações:
    https://www.gov.br/saude/farmaciapopular
    """;

    public static final String END = """
    Obrigado por utilizar o Meu Remédio Popular.

    Lembre-se de que, para a retirada do medicamento em uma farmácia credenciada,
    é obrigatória a apresentação de:
    • Documento oficial com foto
    • Número do CPF
    • Receita médica válida dentro do prazo estabelecido

    Até a próxima! 👋
    """;


    public static final String RESULT_FEEDBACK = """
    Obrigado pelo Feedback!

    Posso ajudár com mais alguma coisa?
    """;

    public static final String BACK_TO_MENU = """
    Voltando ao menu principal...

    Como posso ajudá-lo(a)?
    """;
}


