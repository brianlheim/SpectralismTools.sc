SpectralData {
	// © Brian Heim 2014
	// Spectral.sc library
	// v.0.1 5/8/2014

	// SpectralData is designed as a complement to the functions of
	// Spectral, through these functions:
	// 1.) Static/deterministic playback of data sets generated with
	// Spectral
	// 2.) Dynamic and user-defined playback of these data sets

	// A SpectralData object represents a spectrum which consists
	// of frequency-amplitude pairs.

	// This class has two playback functions. In a nutshell:
	// SpectralData.play is several functions in one: static playback,
	// envelope playback, and playback according to a function operating on
	// every given frequency, amplitude, & index
	// SpectralData.playSpectralEnv plays the data according to a
	// "spectral envelope" (see Spectral.spectralEnvelope)
	var <>freqs, <>amps;

	// constructor takes the 2D array forrmat that most Spectral.sc functions output
	*new {
		arg inArr;
		^super.new.init(inArr[0], inArr[1]);
	}

	// fromArrays is a constructor that takes the two array components of a Spectral.sc
	// spectrum in separate arguments
	*fromArrays {
		arg inFreqs, inAmps;
		^this.new([inFreqs, inAmps]);
	}

	// simple init: this object's frequencies and amplitudes are the ones provided to the constructor
	init {
		arg inFreqs, inAmps;
		freqs = inFreqs;
		amps = inAmps;
	}

	// play this data set: creates, sends, and returns a synth that plays
	// the frequencies with their corresponding amplitudes.
	// optional name may be provided, although this is a trivial argument
	// env and func are two arguments for specifying a playback envelope and function
	// the default envelope is an ASR held open by the \gate argument to the synthdef
	// the function takes three arguments: frequency, amplitude, and index within the spectrum,
	// and the func argument is wrapped within the SynthDef so that it operates on each
	// frequency-amplitude pair in turn. the function should return a signal.
	// this can be useful for making decisions based on which partial or sideband is being passed
	// to the function.
	playSpectrum {
		arg amp=1,
		out=0,
		name=\temp,
		env=Env.asr(0.1, 1, 0.5),
		func={|freq, amp, index| SinOsc.ar(freq, mul:amp)},
		globalFunc={|sig| sig};

		var synth = Synth.basicNew(name);
		SynthDef.new(name, {
			arg freqs, amps, env, gate=1, amp, out;
			var sig = 0;

			freqs.do {
				|item, i|
				sig = sig + SynthDef.wrap(func, prependArgs:[item, amps[i], i]);
			};
			sig = SynthDef.wrap(globalFunc, prependArgs:[sig]) * amp * EnvGen.ar(env, gate, doneAction:2);
			Out.ar(out, Pan2.ar(sig));
			},
			[0, 0.005],
			[this.freqs, this.amps, env]
		).send(Server.local, synth.newMsg(args:[\amp, amp, \out, out]));
		^synth;
	}

	makeSynthDef {
		arg name=\temp,
		func={|freq, amp, index| SinOsc.ar(freq, mul:amp)},
		globalFunc={|sig| sig};

		SynthDef(name, {
			arg freqs, amps, amp=1, out=0;
			var sig = 0;

			freqs.do {
				|item, i|
				sig = sig + SynthDef.wrap(func, prependArgs:[item, amps[i], i]);
			};
			sig = SynthDef.wrap(globalFunc, prependArgs:[sig]) * amp;
			Out.ar(out, Pan2.ar(sig));
			},
			[0, 0.005],
			[this.freqs, this.amps]
		).add;
	}

	// interpolates between two spectra over dur seconds. Can also use env and func arguments as with .play
	playInterpolate {
		arg sd2,
		amp=1,
		out=0,
		name=\temp,
		dur=10, // duration of the interpolation in seconds
		env=Env.asr(0.1, 1, 0.5), // global envelope (useful for closing the gate before completion)
		func={|freq, amp, index| SinOsc.ar(freq, mul:amp)},
		globalFunc={|sig| sig};

		var synth = Synth.basicNew(name);
		SynthDef.new(name, {
			arg freqs, amps, env, gate=1, amp, out;
			var sig = 0, freqln, ampln;

			freqs.do {
				|item, i|
				freqln = XLine.ar(item, sd2.freqs[i], dur);
				ampln = XLine.ar(amps[i], sd2.amps[i], dur);
				sig = sig + SynthDef.wrap(func, prependArgs:[freqln, ampln, i]);
			};
			sig = SynthDef.wrap(globalFunc, prependArgs:[sig]) * amp * EnvGen.ar(env, gate, doneAction:2);
			Out.ar(out, Pan2.ar(sig));
			},
			[0, 0.005],
			[this.freqs, this.amps, env]
		).send(Server.local, synth.newMsg(args:[\amp, amp, \out, out]));
		^synth;
	}


	// play the data set with the method described in Spectral.spectralEnvelope.
	// higher indices in the array are never played before and never leave after lower indices.
	// in most cases this results in a "pyramid" formation, with the lowest indices extending in time
	// before and after higher indices.
	playSpectralEnv {
		arg	amp = 1,
		attack = 0.1, // attack time for partials
		attackOff = 0.1, // delay between successive partial entries
		decay = 0.1, // decay time for partials
		decayOff = 0.1, // delay between successive partial exits
		cAttack = 0, // curve of attack
		cDecay = 0, // curve of decay
		dur = 10, // total duration of the event
		out = 0,
		name = \temp;

		// exact same envelope array as in Spectral.spectralEnvelope
		var synth, aEnv = Array.fill(this.freqs.size, {
			|i|
			var env, tSus = dur - (attackOff * i) - (decayOff * i) - attack - decay;
			env = case
			{ tSus >= 0 }
			{ Env([0, 0, amps[i], amps[i], 0],
				[attackOff * i, attack, tSus, decay],
				[0, cAttack, 0, cDecay]) }
			{ tSus.abs < (attack + decay) } // if the envelope is too short to sustain but
			// long enough to produce some sound
			{ // simplification for all linear curves--very rarely occurring anyway
				// tSus is the distance between the start of the decay and end of the attack
				// in this specific, case, there's an overlap
				var fraction = (attack + decay + tSus) / (attack + decay);
				Env([0, 0, amps[i] * fraction, 0],
					[attackOff * i, attack * fraction, decay * fraction],
					[0, cAttack, 0, cDecay]) }
			{ tSus.abs >= (attack + decay) }
			{ Env([0])};

		});

		// make the synth
		synth = Synth.basicNew(name);
		SynthDef.new(name, {
			arg freqs, amps, amp, out;
			var sig = 0;
			Line.ar(dur:dur, doneAction:2);
			freqs.do {
				|item, i|
				sig = sig + (SinOsc.ar(item, mul:EnvGen.ar(aEnv[i])));
			};
			sig = sig * amp;
			Out.ar(out, Pan2.ar(sig));
			}
			, [0, 0.005], [this.freqs, this.amps]
		).send(Server.local, synth.newMsg(args:[\amp, amp, \out, out]));

		^synth;
	}
}