import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;
import java.io.File;

/**
 * CLASSE: ByteStreamExample
 * DESCRIÇÃO: Demonstração avançada de Byte Streams em Java para operações de
 * leitura e escrita de arquivos a nível de bytes. Esta implementação inclui
 * funcionalidades robustas para monitoramento, tratamento de erros e análise
 * de performance.
 * 
 * PRINCIPAIS CARACTERÍSTICAS AVANÇADAS:
 * - Leitura e escrita byte-a-byte com monitoramento em tempo real
 * - Sistema abrangente de estatísticas e métricas de performance
 * - Tratamento robusto de exceções com múltiplos níveis de recuperação
 * - Validações pré-operacionais de arquivos e permissões
 * - Sistema de logging detalhado para debugging e auditoria
 * - Múltiplas estratégias de fallback e recuperação de erros
 */
class ByteStreamExample {

    // CONSTANTES PARA CONFIGURAÇÃO
    private static final int PROGRESS_UPDATE_INTERVAL = 100; // Bytes entre atualizações de progresso
    private static final int LARGE_FILE_THRESHOLD = 1024 * 1024; // 1MB threshold para arquivos grandes
    private static final String BACKUP_EXTENSION = ".backup";

    /**
     * MÉTODO PRINCIPAL - Coordena toda a operação de cópia de arquivo
     * 
     * @param ar - Array de argumentos da linha de comando (pode conter caminhos de
     *           arquivos)
     * @throws IOException - Propaga exceções críticas de I/O para o runtime
     */
    public static void main(String[] ar) throws IOException {
        // CONFIGURAÇÃO DOS CAMINHOS - permite override por argumentos
        String sourceFile = (ar.length > 0) ? ar[0] : "src/source.txt";
        String destFile = (ar.length > 1) ? ar[1] : "src/dest.txt";

        // EXECUÇÃO DA OPERAÇÃO PRINCIPAL
        boolean success = performByteCopyOperation(sourceFile, destFile);

        // VERIFICAÇÃO FINAL DO RESULTADO
        if (success) {
            System.out.println("🎉 OPERAÇÃO FINALIZADA COM SUCESSO TOTAL!");
            performPostCopyVerification(sourceFile, destFile);
        } else {
            System.out.println("❌ OPERAÇÃO FINALIZADA COM FALHAS!");
            System.exit(1);
        }
    }

    /**
     * REALIZA A OPERAÇÃO DE CÓPIA BYTE-A-BYTE COM TODOS OS CONTROLES
     * 
     * @param sourceFile - Caminho do arquivo fonte
     * @param destFile   - Caminho do arquivo destino
     * @return boolean - true se a operação foi bem sucedida
     */
    private static boolean performByteCopyOperation(String sourceFile, String destFile) {
        // DECLARAÇÃO DAS STREAMS - inicializadas como null para segurança no finally
        FileInputStream inStream = null;
        FileOutputStream outStream = null;

        // SISTEMA AVANÇADO DE MONITORAMENTO E ESTATÍSTICAS
        long startTime = System.currentTimeMillis();
        long operationStartTime = startTime;
        int totalBytesRead = 0;
        int lastProgressUpdate = 0;
        boolean operationSuccessful = false;

        try {
            // FASE 1: PRÉ-VALIDAÇÕES E INICIALIZAÇÃO
            printOperationHeader("FASE 1: PRÉ-VALIDAÇÕES E INICIALIZAÇÃO");

            if (!performPreOperationValidations(sourceFile, destFile)) {
                return false;
            }

            // FASE 2: INICIALIZAÇÃO DAS STREAMS
            printOperationHeader("FASE 2: INICIALIZAÇÃO DAS STREAMS");

            long initStartTime = System.currentTimeMillis();
            inStream = new FileInputStream(sourceFile);
            outStream = new FileOutputStream(destFile);
            long initTime = System.currentTimeMillis() - initStartTime;

            System.out.println(" Streams inicializadas com sucesso!");
            System.out.println("   Tempo de inicialização: " + initTime + " ms");
            System.out.println("   Tamanho do arquivo fonte: " + new File(sourceFile).length() + " bytes");

            // FASE 3: OPERAÇÃO DE CÓPIA BYTE-A-BYTE
            printOperationHeader("FASE 3: OPERAÇÃO DE CÓPIA BYTE-A-BYTE");

            System.out.println("Iniciando processo de cópia byte-a-byte...");
            System.out.println("   Intervalo de progresso: a cada " + PROGRESS_UPDATE_INTERVAL + " bytes");

            int content;
            long copyStartTime = System.currentTimeMillis();

            /**
             * LOOP PRINCIPAL DE ALTA PRECISÃO - BYTE A BYTE
             * CARACTERÍSTICAS TÉCNICAS:
             * - Precisão absoluta: cada byte é processado individualmente
             * - Baixo consumo de memória: máximo 1 byte na memória por vez
             * - Controle granular: possível interromper a qualquer momento
             * - Ideal para: arquivos pequenos, operações críticas, debugging
             * 
             * CICLO DE PROCESSAMENTO:
             * 1. read() → lê um byte (0-255) ou retorna -1 (EOF)
             * 2. Conversão int → byte mantendo integridade dos dados
             * 3. write() → escreve no destino garantindo ordem sequencial
             * 4. Monitoramento → atualiza estatísticas em tempo real
             */
            while ((content = inStream.read()) != -1) {
                // CONVERSÃO SEGURA DE INT PARA BYTE
                // Preserva apenas os 8 bits menos significativos
                byte byteToWrite = (byte) content;

                // ESCRITA NO ARQUIVO DESTINO
                // Operação atômica - cada byte é escrito imediatamente
                outStream.write(byteToWrite);

                // ATUALIZAÇÃO DE ESTATÍSTICAS E MONITORAMENTO
                totalBytesRead++;

                // SISTEMA DE PROGRESSO COM FEEDBACK VISUAL
                if (totalBytesRead - lastProgressUpdate >= PROGRESS_UPDATE_INTERVAL) {
                    printProgressUpdate(totalBytesRead, startTime);
                    lastProgressUpdate = totalBytesRead;
                }

                // VERIFICAÇÃO DE INTERRUPÇÃO (para sistemas interativos)
                if (Thread.currentThread().isInterrupted()) {
                    System.out.println("⚠ Operação interrompida pelo usuário!");
                    break;
                }
            }

            long copyTime = System.currentTimeMillis() - copyStartTime;

            // FASE 4: ANÁLISE DE PERFORMANCE E RELATÓRIO
            printOperationHeader("FASE 4: ANÁLISE DE PERFORMANCE E RELATÓRIO");

            operationSuccessful = true;
            generatePerformanceReport(startTime, totalBytesRead, operationStartTime, initTime, copyTime);

        } catch (IOException e) {
            // SISTEMA AVANÇADO DE TRATAMENTO DE ERROS
            printOperationHeader("FASE DE TRATAMENTO DE ERROS");
            handleCopyOperationError(e, sourceFile, destFile, totalBytesRead);
            operationSuccessful = false;

        } finally {
            // FASE 5: GERENCIAMENTO DE RECURSOS E LIMPEZA
            printOperationHeader("FASE 5: GERENCIAMENTO DE RECURSOS");
            performResourceCleanup(inStream, outStream);
        }

        return operationSuccessful;
    }

    /**
     * REALIZA VALIDAÇÕES PRÉ-OPERACIONAIS COMPLETAS
     */
    private static boolean performPreOperationValidations(String sourceFile, String destFile) {
        System.out.println(" Realizando validações pré-operacionais...");

        File source = new File(sourceFile);
        File dest = new File(destFile);

        // VALIDAÇÃO DO ARQUIVO FONTE
        if (!source.exists()) {
            System.err.println(" ERRO: Arquivo fonte não encontrado: " + sourceFile);
            System.err.println("   Caminho absoluto: " + source.getAbsolutePath());
            return false;
        }

        if (!source.canRead()) {
            System.err.println(" ERRO: Sem permissão de leitura no arquivo fonte: " + sourceFile);
            return false;
        }

        if (source.length() == 0) {
            System.out.println(" AVISO: Arquivo fonte está vazio!");
        }

        // VALIDAÇÃO DO ARQUIVO DESTINO
        if (dest.exists()) {
            System.out.println(" AVISO: Arquivo destino já existe e será sobrescrito!");

            // CRIA BACKUP AUTOMÁTICO PARA ARQUIVOS EXISTENTES
            try {
                createBackup(destFile);
            } catch (IOException e) {
                System.err.println(" AVISO: Não foi possível criar backup: " + e.getMessage());
            }
        }

        // VERIFICAÇÃO DE ESPAÇO EM DISCO
        long requiredSpace = source.length();
        long availableSpace = dest.getParentFile().getUsableSpace();

        if (requiredSpace > availableSpace) {
            System.err.println(" ERRO: Espaço em disco insuficiente!");
            System.err.println("   Espaço necessário: " + requiredSpace + " bytes");
            System.err.println("   Espaço disponível: " + availableSpace + " bytes");
            return false;
        }

        // VERIFICAÇÃO DE PERFORMANCE PARA ARQUIVOS GRANDES
        if (source.length() > LARGE_FILE_THRESHOLD) {
            System.out.println(" AVISO: Arquivo grande detectado (" + source.length() + " bytes)");
            System.out.println("   Recomendação: Considere usar buffered streams para melhor performance");
        }

        System.out.println(" Todas as validações pré-operacionais passaram!");
        return true;
    }

    /**
     * CRIA BACKUP DO ARQUIVO DESTINO EXISTENTE
     */
    private static void createBackup(String destFile) throws IOException {
        File original = new File(destFile);
        File backup = new File(destFile + BACKUP_EXTENSION);

        try (FileInputStream backupIn = new FileInputStream(original);
                FileOutputStream backupOut = new FileOutputStream(backup)) {

            int content;
            while ((content = backupIn.read()) != -1) {
                backupOut.write((byte) content);
            }
        }

        System.out.println("   Backup criado: " + backup.getName());
    }

    /**
     * ATUALIZAÇÃO DE PROGRESSO COM ESTATÍSTICAS EM TEMPO REAL
     */
    private static void printProgressUpdate(int bytesProcessed, long startTime) {
        long currentTime = System.currentTimeMillis();
        long elapsedTime = currentTime - startTime;
        double bytesPerSecond = (elapsedTime > 0) ? (bytesProcessed * 1000.0) / elapsedTime : 0;

        System.out.printf("    Progresso: %,d bytes | Velocidade: %,.2f bytes/segundo%n",
                bytesProcessed, bytesPerSecond);
    }

    /**
     * RELATÓRIO COMPLETO DE PERFORMANCE
     */
    private static void generatePerformanceReport(long startTime, int totalBytes,
            long operationStart, long initTime, long copyTime) {
        long totalTime = System.currentTimeMillis() - operationStart;
        long endTime = System.currentTimeMillis();

        double bytesPerSecond = (copyTime > 0) ? (totalBytes * 1000.0) / copyTime : 0;
        double totalBytesPerSecond = (totalTime > 0) ? (totalBytes * 1000.0) / totalTime : 0;

        System.out.println(" === RELATÓRIO DETALHADO DE PERFORMANCE ===");
        System.out.println("   Bytes copiados: " + formatNumberWithCommas(totalBytes));
        System.out.println("   Tempo total da operação: " + totalTime + " ms");
        System.out.println("   - Inicialização: " + initTime + " ms");
        System.out.println("   - Cópia: " + copyTime + " ms");
        System.out.println("   - Limpeza: " + (totalTime - initTime - copyTime) + " ms");
        System.out.printf("   Velocidade de cópia: %,.2f bytes/segundo%n", bytesPerSecond);
        System.out.printf("   Velocidade total: %,.2f bytes/segundo%n", totalBytesPerSecond);
        System.out.println("   Horário de início: " + new Date(startTime));
        System.out.println("   Horário de término: " + new Date(endTime));

        // ANÁLISE DE EFICIÊNCIA
        double efficiency = ((double) copyTime / totalTime) * 100;
        System.out.printf("   Eficiência operacional: %.1f%%%n", efficiency);
    }

    /**
     * FORMATA NÚMEROS COM VÍRGULAS (alternative para String.format)
     */
    private static String formatNumberWithCommas(int number) {
        // Implementação simples para versões antigas do Java
        return String.valueOf(number).replaceAll("\\B(?=(\\d{3})+(?!\\d))", ",");
    }

    /**
     * TRATAMENTO AVANÇADO DE ERROS
     */
    private static void handleCopyOperationError(IOException e, String sourceFile,
            String destFile, int bytesProcessed) {
        System.err.println("*** ERRO CRÍTICO NA OPERAÇÃO DE CÓPIA ***");
        System.err.println("   Tipo: " + e.getClass().getSimpleName());
        System.err.println("   Mensagem: " + e.getMessage());
        System.err.println("   Arquivo fonte: " + sourceFile);
        System.err.println("   Arquivo destino: " + destFile);
        System.err.println("   Bytes processados antes do erro: " + bytesProcessed);

        // TENTATIVA DE LIMPEZA DO ARQUIVO CORROMPIDO
        try {
            File corruptedFile = new File(destFile);
            if (corruptedFile.exists() && bytesProcessed < corruptedFile.length()) {
                corruptedFile.delete();
                System.err.println("   Arquivo destino parcial foi removido devido ao erro");
            }
        } catch (SecurityException se) {
            System.err.println("   Não foi possível remover arquivo corrompido: " + se.getMessage());
        }

        // SUGESTÕES DE RECUPERAÇÃO
        System.err.println("    SUGESTÕES:");
        System.err.println("   - Verifique permissões de arquivo");
        System.err.println("   - Confirme que o arquivo fonte não está corrompido");
        System.err.println("   - Verifique espaço em disco disponível");
        System.err.println("   - Tente executar como administrador se necessário");
    }

    /**
     * GERENCIAMENTO SEGURO DE RECURSOS
     */
    private static void performResourceCleanup(FileInputStream inStream, FileOutputStream outStream) {
        System.out.println(" Realizando limpeza de recursos...");

        int closedStreams = 0;

        // FECHAMENTO DA INPUT STREAM COM PROTEÇÃO MÁXIMA
        if (inStream != null) {
            try {
                inStream.close();
                System.out.println("    Input stream fechada com sucesso");
                closedStreams++;
            } catch (IOException closeException) {
                System.err.println("    ERRO ao fechar input stream: " + closeException.getMessage());
                // Em casos críticos, poderia tentar force-close aqui
            }
        }

        // FECHAMENTO DA OUTPUT STREAM COM PROTEÇÃO MÁXIMA
        if (outStream != null) {
            try {
                outStream.close();
                System.out.println("    Output stream fechada com sucesso");
                closedStreams++;
            } catch (IOException closeException) {
                System.err.println("    ERRO ao fechar output stream: " + closeException.getMessage());
            }
        }

        System.out.println("    Resumo de limpeza: " + closedStreams + "/2 streams fechadas");
        System.out.println(" === RECURSOS LIBERADOS ===");
    }

    /**
     * VERIFICAÇÃO PÓS-OPERAÇÃO
     */
    private static void performPostCopyVerification(String sourceFile, String destFile) {
        System.out.println("\n Realizando verificação pós-cópia...");

        File source = new File(sourceFile);
        File dest = new File(destFile);

        if (source.length() == dest.length()) {
            System.out.println(" VERIFICAÇÃO: Tamanhos dos arquivos coincidem!");
            System.out.println("   Tamanho fonte: " + source.length() + " bytes");
            System.out.println("   Tamanho destino: " + dest.length() + " bytes");
        } else {
            System.out.println("⚠ AVISO: Tamanhos dos arquivos diferem!");
            System.out.println("   Tamanho fonte: " + source.length() + " bytes");
            System.out.println("   Tamanho destino: " + dest.length() + " bytes");
        }
    }

    /**
     * HEADER PARA ORGANIZAÇÃO VISUAL DAS FASES - VERSÃO COMPATÍVEL
     */
    private static void printOperationHeader(String phaseName) {
        System.out.println("\n" + generateLine(60));
        System.out.println(" " + phaseName);
        System.out.println(generateLine(60));
    }

    /**
     * GERA LINHA DE SEPARAÇÃO - alternativa para repeat()
     */
    private static String generateLine(int length) {
        StringBuilder line = new StringBuilder();
        for (int i = 0; i < length; i++) {
            line.append("=");
        }
        return line.toString();
    }
}