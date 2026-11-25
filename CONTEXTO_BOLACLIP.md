# Contexto Atual — BolaClip (com.clipworks.bolaclip)
- Autor / Desenvolvedor: Esteffeson Matheus Barquete (Clip Works Tecnologia)
- Repositório: Bolaclip67 (branch main)
- Stack: Gradle 9.9.1, AGP 8.7.3, Kotlin 1.9.22, Java 17, compileSdk/targetSdk 34, minSdk 24, Firebase Realtime Database, overlay TYPE_APPLICATION_OVERLAY (file:///android_asset/index.html?overlay=1).
- Tema & Identidade: Gold & Salmon (#FDE68A, #F59E0B, #FCA5A5, #FB7185), bolha matriz (+), cofre/gaveta, modal de créditos, rodapé de licença, console secreto (Therion@22), sem depender de capital inicial.
- Último estado (commit f4516ea):
  * FloatingService.kt restaurado e ajustado para alternar FLAG_NOT_FOCUSABLE com segurança e showSoftInput/hideSoftInputFromWindow sem quebras de sintaxe.
  * index.html ajustado no fechar/abrir do bubbleModal, tolerância de duplo toque em bindBubbleGestures (> 14px) e transform/visibilidade do vaultDrawer.
- Próximo passo após o descanso:
  1. Ver o resultado do build no GitHub Actions e instalar o APK.
  2. Testar na prática: botão Cancelar no modal, 2 cliques nas bolinhas normais (editar), abertura do menu/gaveta e teclado no overlay/modal.
  3. Após validar tudo funcionando, empacotar o código-fonte final (ZIP/PDF) para revenda/re-keying.
