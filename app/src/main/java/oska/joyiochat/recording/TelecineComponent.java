package oska.joyiochat.recording;

import javax.inject.Singleton;

import dagger.Component;

// Component is an interface
// decide how to construct in the module

/**
* injected by :
 * 1. TelecineComponent
 * 2. TelecineService
* */
@Singleton
@Component(modules = TelecineModule.class)
interface TelecineComponent {
  void inject(LetterRecordActivity activity);

  void inject(TelecineService service);
}
