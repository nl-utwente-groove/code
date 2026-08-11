<?xml version="1.0" encoding="UTF-8"?>
<gxl xmlns="http://www.gupro.de/GXL/gxl-1.0.dtd">
    <graph id="graph" role="graph" edgeids="false" edgemode="directed">
        <node id="n93"/>
        <node id="n96"/>
        <node id="n94"/>
        <node id="n95"/>
        <edge from="n96" to="n96">
            <attr name="label">
                <string>not:</string>
            </attr>
        </edge>
        <edge from="n95" to="n95">
            <attr name="label">
                <string>new</string>
            </attr>
        </edge>
        <edge from="n94" to="n94">
            <attr name="label">
                <string>del</string>
            </attr>
        </edge>
        <edge from="n93" to="n94">
            <attr name="label">
                <string>del:a</string>
            </attr>
        </edge>
        <edge from="n93" to="n93">
            <attr name="label">
                <string>use</string>
            </attr>
        </edge>
        <edge from="n94" to="n96">
            <attr name="label">
                <string>not:c.a</string>
            </attr>
        </edge>
        <edge from="n93" to="n95">
            <attr name="label">
                <string>new:b</string>
            </attr>
        </edge>
    </graph>
</gxl>
